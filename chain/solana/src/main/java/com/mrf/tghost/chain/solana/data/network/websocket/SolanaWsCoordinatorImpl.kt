package com.mrf.tghost.chain.solana.data.network.websocket

import android.util.Log
import com.mrf.tghost.chain.solana.data.network.mappers.toDomainModel
import com.mrf.tghost.chain.solana.data.network.mappers.toSolanaStake
import com.mrf.tghost.chain.solana.domain.model.DasApiResponse
import com.mrf.tghost.chain.solana.domain.model.SolanaSplTokenAccount
import com.mrf.tghost.chain.solana.domain.model.SolanaStake
import com.mrf.tghost.chain.solana.data.network.resolver.websocket.SolanaWsResolver
import com.mrf.tghost.chain.solana.domain.network.SolanaWsCoordinator
import com.mrf.tghost.chain.solana.utils.SOLANA_SPL_TOKEN_PROGRAM_ID
import com.mrf.tghost.chain.solana.utils.SOLANA_STAKE_PROGRAM_ID
import com.mrf.tghost.domain.model.Result
import com.mrf.tghost.domain.network.WebSocketManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "SolanaWsCoordinator"
private const val SOLANA_WS_CONNECT_DELAY_MS = 800L
private const val SUBSCRIPTION_ID_TIMEOUT_MS = 5_000L

@Singleton
class SolanaWsCoordinatorImpl @Inject constructor(
    private val webSocketManager: WebSocketManager,
    private val solanaWsResolver: SolanaWsResolver,
) : SolanaWsCoordinator {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var solanaWsUrl: String? = null
    private val pending = ConcurrentHashMap<String, PendingEntry>()
    /** Handlers by subscription id (server may use this in params.subscription). */
    private val active = ConcurrentHashMap<Long, ActiveEntry>()
    /** Handlers by request id (UUID string) when server echoes it in notifications. */
    private val activeByRequestId = ConcurrentHashMap<String, ActiveEntry>()
    private val collectorJobRef: AtomicReference<Job?> = AtomicReference(null)

    private sealed class PendingEntry {
        abstract val idChannel: Channel<Long>
        data class AccountBalance(override val idChannel: Channel<Long>, val notificationChannel: Channel<Long>) : PendingEntry()
        data class TokenAccounts(override val idChannel: Channel<Long>, val notificationChannel: Channel<List<SolanaSplTokenAccount>>) : PendingEntry()
        data class StakingAccounts(override val idChannel: Channel<Long>, val notificationChannel: Channel<List<SolanaStake>>) : PendingEntry()
        data class NftAccounts(override val idChannel: Channel<Long>, val notificationChannel: Channel<DasApiResponse>) : PendingEntry()
    }

    private fun interface ActiveEntry {
        fun onNotification(message: String)
    }

    /** @return true if connected (or already connected), false if Solana WS URL is not available */
    private suspend fun ensureCollector(): Boolean {
        val url = try {
            solanaWsResolver.resolveSolanaWsUrl() ?: return false
        } catch (e: Exception) {
            Log.e(TAG, "ensureCollector: Error resolving Solana WS URL", e)
            return false
        }

        val existing = collectorJobRef.get()
        val urlChanged = solanaWsUrl != null && solanaWsUrl != url

        if (existing?.isActive == true && !urlChanged) {
            webSocketManager.connectByUrl(url)
            return true
        }

        if (urlChanged) {
            Log.i(TAG, "ensureCollector: URL changed from $solanaWsUrl to $url. Resetting collector.")
            existing?.cancel()
            active.clear()
            activeByRequestId.clear()
            pending.clear()
        }

        solanaWsUrl = url
        webSocketManager.connectByUrl(url)

        val newJob = scope.launch {
            try {
                delay(SOLANA_WS_CONNECT_DELAY_MS)
                val flow = webSocketManager.messageFlowByUrl(url)
                flow.collect { msg ->
                    parseSolanaSubscriptionResponse(msg)?.let { (requestId, subscriptionId) ->
                        val entry = pending.remove(requestId) ?: run {
                            Log.w(TAG, "collector: subscription response but no pending entry for requestId")
                            return@collect
                        }
                        entry.idChannel.trySend(subscriptionId)
                        entry.idChannel.close()
                        when (entry) {
                            is PendingEntry.AccountBalance -> {
                                val handler = ActiveEntry { m ->
                                    val lamports = parseSolanaAccountNotificationLamports(m, subscriptionId)
                                        ?: parseSolanaAccountNotificationLamports(m, null)
                                    lamports?.let { entry.notificationChannel.trySend(it) }
                                }
                                active[subscriptionId] = handler
                                activeByRequestId[requestId] = handler
                            }
                            is PendingEntry.TokenAccounts -> {
                                val list = mutableListOf<SolanaSplTokenAccount>()
                                val handler = ActiveEntry { m ->
                                    val dto = parseSolanaProgramNotificationAccount(m, subscriptionId)
                                    dto?.let {
                                        val domain = it.toDomainModel()
                                        list.removeAll { elem -> elem.pubkey == domain.pubkey }
                                        list.add(domain)
                                        entry.notificationChannel.trySend(list.toList())
                                    }
                                }
                                active[subscriptionId] = handler
                                activeByRequestId[requestId] = handler
                            }
                            is PendingEntry.StakingAccounts -> {
                                val list = mutableListOf<SolanaStake>()
                                val handler = ActiveEntry { m ->
                                    val dto = parseSolanaStakeNotificationAccount(m, subscriptionId)
                                    dto?.let {
                                        val stake = it.toSolanaStake()
                                        list.removeAll { elem -> elem.validatorAddress == stake.validatorAddress }
                                        list.add(stake)
                                        entry.notificationChannel.trySend(list.toList())
                                    }
                                }
                                active[subscriptionId] = handler
                                activeByRequestId[requestId] = handler
                            }
                            is PendingEntry.NftAccounts -> {
                                val handler = ActiveEntry { m ->
                                    parseSolanaNftNotification(m, requestId)?.let { dto ->
                                        entry.notificationChannel.trySend(dto.toDomainModel())
                                    }
                                }
                                active[subscriptionId] = handler
                                activeByRequestId[requestId] = handler
                            }
                        }
                        return@collect
                    }
                    val idNum = parseSolanaNotificationSubscriptionId(msg)
                    val idString = parseSolanaNotificationIdAsString(msg)
                    val handler = (idNum?.let { active[it] }) ?: (idString?.let { activeByRequestId[it] })
                    if (idNum != null || idString != null) handler?.onNotification(msg)
                }
                Log.w(TAG, "collector: messageFlow ended (connection closed)")
            } finally {
                collectorJobRef.set(null)
            }
        }

        if (!collectorJobRef.compareAndSet(existing, newJob)) {
            newJob.cancel()
        }
        return true
    }

    override fun subscribeAccountBalance(publicKey: String): Flow<Result<Long>> = flow {
        val shortKey = publicKey.take(8) + "…"
        Log.d(TAG, "subscribeAccountBalance: start pubkey=$shortKey")
        if (!ensureCollector()) {
            emit(Result.Failure("Failed to ensure collector (WS URL might be invalid)"))
            return@flow
        }
        val (body, requestId) = buildSolanaAccountSubscribeBody(publicKey)
        val idChannel = Channel<Long>(Channel.RENDEZVOUS)
        val notificationChannel = Channel<Long>(Channel.CONFLATED)
        pending[requestId] = PendingEntry.AccountBalance(idChannel, notificationChannel)
        Log.d(TAG, "subscribeAccountBalance: send subscribe requestId=$requestId pubkey=$shortKey")
        solanaWsUrl?.let { webSocketManager.sendByUrl(it, body) }
        val subscriptionId = withTimeoutOrNull(SUBSCRIPTION_ID_TIMEOUT_MS) { idChannel.receive() }
        if (subscriptionId == null) {
            Log.w(TAG, "subscribeAccountBalance: timeout waiting for subscription id")
            emit(Result.Failure("Timeout waiting for Solana subscription ID"))
            return@flow
        }
        Log.d(TAG, "subscribeAccountBalance: got subId=$subscriptionId pubkey=$shortKey")
        for (lamports in notificationChannel) {
            emit(Result.Success(lamports))
        }
    }.flowOn(Dispatchers.IO)

    override fun subscribeTokenAccounts(publicKey: String): Flow<Result<List<SolanaSplTokenAccount>>> = flow {
        if (!ensureCollector()) {
            emit(Result.Failure("Failed to ensure collector"))
            return@flow
        }
        val (body, requestId) = buildSolanaProgramSubscribeBody(SOLANA_SPL_TOKEN_PROGRAM_ID, publicKey)
        val idChannel = Channel<Long>(Channel.RENDEZVOUS)
        val notificationChannel = Channel<List<SolanaSplTokenAccount>>(Channel.CONFLATED)
        pending[requestId] = PendingEntry.TokenAccounts(idChannel, notificationChannel)
        solanaWsUrl?.let { webSocketManager.sendByUrl(it, body) }
        val subscriptionId = withTimeoutOrNull(SUBSCRIPTION_ID_TIMEOUT_MS) { idChannel.receive() }
        if (subscriptionId == null) {
            emit(Result.Failure("Timeout waiting for subscription ID"))
            return@flow
        }
        for (accounts in notificationChannel) {
            emit(Result.Success(accounts))
        }
    }.flowOn(Dispatchers.IO)

    override fun subscribeStakingAccounts(walletAddress: String): Flow<Result<List<SolanaStake>>> = flow {
        if (!ensureCollector()) {
            emit(Result.Failure("Failed to ensure collector"))
            return@flow
        }
        val (body, requestId) = buildSolanaProgramSubscribeBody(SOLANA_STAKE_PROGRAM_ID, walletAddress)
        val idChannel = Channel<Long>(Channel.RENDEZVOUS)
        val notificationChannel = Channel<List<SolanaStake>>(Channel.CONFLATED)
        pending[requestId] = PendingEntry.StakingAccounts(idChannel, notificationChannel)
        solanaWsUrl?.let { webSocketManager.sendByUrl(it, body) }
        val subscriptionId = withTimeoutOrNull(SUBSCRIPTION_ID_TIMEOUT_MS) { idChannel.receive() }
        if (subscriptionId == null) {
            emit(Result.Failure("Timeout waiting for subscription ID"))
            return@flow
        }
        for (accounts in notificationChannel) {
            emit(Result.Success(accounts))
        }
    }.flowOn(Dispatchers.IO)

    override fun subscribeNftAccounts(publicKey: String): Flow<Result<DasApiResponse>> = flow {
        if (!ensureCollector()) {
            emit(Result.Failure("Failed to ensure collector"))
            return@flow
        }
        val (body, requestId) = buildSolanaNftSubscribeBody(publicKey)
        val idChannel = Channel<Long>(Channel.RENDEZVOUS)
        val notificationChannel = Channel<DasApiResponse>(Channel.CONFLATED)
        pending[requestId] = PendingEntry.NftAccounts(idChannel, notificationChannel)
        solanaWsUrl?.let { webSocketManager.sendByUrl(it, body) }
        val subscriptionId = withTimeoutOrNull(SUBSCRIPTION_ID_TIMEOUT_MS) { idChannel.receive() }
        if (subscriptionId == null) {
            emit(Result.Failure("Timeout waiting for subscription ID"))
            return@flow
        }
        for (nfts in notificationChannel) {
            emit(Result.Success(nfts))
        }
    }.flowOn(Dispatchers.IO)
}
