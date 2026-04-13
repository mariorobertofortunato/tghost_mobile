package com.mrf.tghost.chain.solana.data.repository

import com.mrf.tghost.chain.solana.data.network.http.rpcrequests.getSolanaBalanceRequest
import com.mrf.tghost.chain.solana.data.network.model.SolanaRpcResponseDto
import com.mrf.tghost.chain.solana.data.network.resolver.http.SolanaHttpResolver
import com.mrf.tghost.chain.solana.domain.network.SolanaWsCoordinator
import com.mrf.tghost.chain.solana.domain.repository.SolanaWalletBalanceRepository
import com.mrf.tghost.chain.solana.utils.POLL_MS
import com.mrf.tghost.data.datastore.DataStore
import com.mrf.tghost.data.network.http.factory.RpcRequestFactory
import com.mrf.tghost.domain.model.Result
import com.mrf.tghost.domain.model.isSuccess
import com.solana.rpccore.Rpc20Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.merge
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import javax.inject.Inject

class SolanaWalletBalanceRepositoryImpl @Inject constructor(
    private val solanaWsCoordinator: SolanaWsCoordinator,
    private val solanaHttpResolver: SolanaHttpResolver,
    private val dataStore: DataStore
) : SolanaWalletBalanceRepository {

    override fun balanceSolana(publicKey: String): Flow<Result<Long>> = channelFlow {
        val httpResult = getBalanceSolanaHttp(publicKey)
        send(httpResult)
        var lastLamports: Long? = (httpResult as? Result.Success)?.data

        dataStore.getLiveUpdateStatus.collectLatest { liveUpdateEnabled ->
            if (!liveUpdateEnabled) return@collectLatest
            val wsFlow = solanaWsCoordinator.subscribeAccountBalance(publicKey)
            val pollFlow = flow {
                while (true) {
                    delay(POLL_MS)
                    (getBalanceSolanaHttp(publicKey) as? Result.Success)?.data?.let { emit(Result.Success(it)) }
                }
            }
            merge(wsFlow, pollFlow).collect { result ->
                if (result.isSuccess() && result.data != lastLamports) {
                    lastLamports = result.data
                    send(result)
                }
            }
        }
    }.flowOn(Dispatchers.IO)

    private suspend fun getBalanceSolanaHttp(publicKey: String): Result<Long> {
        val url = solanaHttpResolver.resolveSolanaUrl()
        val response: Rpc20Response<SolanaRpcResponseDto<Long?>> =
            RpcRequestFactory.makeRpcRequest(
                url = url,
                request = getSolanaBalanceRequest(publicKey),
                resultSerializer = SolanaRpcResponseDto.serializer(Long.serializer().nullable)
            )
        return if (response.error != null) {
            Result.Failure("${response.error?.code}, ${response.error?.message}")
        } else {
            Result.Success(response.result?.value ?: 0)
        }
    }
}
