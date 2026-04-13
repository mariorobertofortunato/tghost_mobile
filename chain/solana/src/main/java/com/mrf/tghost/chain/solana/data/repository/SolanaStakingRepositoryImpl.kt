package com.mrf.tghost.chain.solana.data.repository

import com.mrf.tghost.chain.solana.data.network.http.rpcrequests.getSolanaStakesRequest
import com.mrf.tghost.chain.solana.data.network.mappers.toSolanaStake
import com.mrf.tghost.chain.solana.data.network.model.SolanaStakeAccountDto
import com.mrf.tghost.chain.solana.data.network.resolver.http.SolanaHttpResolver
import com.mrf.tghost.chain.solana.domain.model.SolanaStake
import com.mrf.tghost.chain.solana.domain.network.SolanaWsCoordinator
import com.mrf.tghost.chain.solana.domain.repository.SolanaStakingRepository
import com.mrf.tghost.chain.solana.utils.POLL_MS
import com.mrf.tghost.data.datastore.DataStore
import com.mrf.tghost.data.network.http.factory.RpcRequestFactory
import com.mrf.tghost.domain.model.Result
import com.mrf.tghost.domain.model.isSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.merge
import kotlinx.serialization.builtins.ListSerializer
import javax.inject.Inject

class SolanaStakingRepositoryImpl @Inject constructor(
    private val solanaWsCoordinator: SolanaWsCoordinator,
    private val solanaHttpResolver: SolanaHttpResolver,
    private val dataStore: DataStore
) : SolanaStakingRepository {

    override fun solanaStakingAccounts(publicKey: String): Flow<Result<List<SolanaStake>>> =
        channelFlow {
            val httpResult = getSolanaStakingAccountsHttp(publicKey)
            send(httpResult)
            var lastList: List<SolanaStake>? = (httpResult as? Result.Success)?.data

            dataStore.getLiveUpdateStatus.collectLatest { liveUpdateEnabled ->
                if (!liveUpdateEnabled) return@collectLatest
                val wsFlow = solanaWsCoordinator.subscribeStakingAccounts(publicKey)
                val pollFlow = flow {
                    while (true) {
                        delay(POLL_MS)
                        (getSolanaStakingAccountsHttp(publicKey) as? Result.Success)?.let { emit(it) }
                    }
                }
                merge(wsFlow, pollFlow).collect { result ->
                    if (result.isSuccess() && result.data != lastList) {
                        lastList = result.data
                        send(result)
                    }
                }
            }
        }.flowOn(Dispatchers.IO)

    private suspend fun getSolanaStakingAccountsHttp(walletAddress: String): Result<List<SolanaStake>> {
        val serializer = ListSerializer(SolanaStakeAccountDto.serializer())
        val url = solanaHttpResolver.resolveSolanaUrl()

        val stakerRequest = CoroutineScope(Dispatchers.IO).async {
            RpcRequestFactory.makeRpcRequest(
                url = url,
                request = getSolanaStakesRequest(walletAddress, offset = 12),
                resultSerializer = serializer
            )
        }

        val withdrawerRequest = CoroutineScope(Dispatchers.IO).async {
            RpcRequestFactory.makeRpcRequest(
                url = url,
                request = getSolanaStakesRequest(walletAddress, offset = 44),
                resultSerializer = serializer
            )
        }

        val responses = awaitAll(stakerRequest, withdrawerRequest)

        val allStakes = mutableListOf<SolanaStakeAccountDto>()
        var errorMsg: String? = null

        responses.forEach { response ->
            response.result?.let { allStakes.addAll(it) }
            if (response.error != null && errorMsg == null) {
                errorMsg = "${response.error?.code}, ${response.error?.message}"
            }
        }

        val distinctStakes = allStakes.distinctBy { it.pubkey }.map { it.toSolanaStake() }

        return if (distinctStakes.isNotEmpty()) {
            Result.Success(distinctStakes)
        } else if (errorMsg != null) {
            Result.Failure(errorMsg)
        } else {
            Result.Success(emptyList())
        }
    }

}
