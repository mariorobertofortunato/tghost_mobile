package com.mrf.tghost.chain.evm.data.repository

import com.mrf.tghost.data.network.http.factory.RpcRequestFactory
import com.mrf.tghost.domain.model.EvmChain
import com.mrf.tghost.domain.model.Result
import com.mrf.tghost.chain.evm.data.network.http.rpcrequests.getEvmBalanceRequest
import com.mrf.tghost.chain.evm.data.network.resolver.http.EvmHttpResolver
import com.mrf.tghost.chain.evm.domain.repository.EvmWalletBalanceRepository
import com.mrf.tghost.chain.evm.utils.POLL_MS
import com.mrf.tghost.data.datastore.DataStore
import com.mrf.tghost.domain.model.isSuccess
import com.solana.rpccore.Rpc20Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.merge
import kotlinx.serialization.builtins.serializer
import javax.inject.Inject

class EvmWalletBalanceRepositoryImpl @Inject constructor(
    private val evmHttpResolver: EvmHttpResolver,
    private val dataStore: DataStore
) : EvmWalletBalanceRepository {

    override fun balanceEvm(publicKey: String, evmChainId: EvmChain): Flow<Result<Long>?> = channelFlow {
        val httpResult = getBalanceEvmHttp(publicKey, evmChainId)
        send(httpResult)
        var lastAmount: Long? = (httpResult as? Result.Success)?.data

        dataStore.getLiveUpdateStatus.collectLatest { liveUpdateEnabled ->
            if (!liveUpdateEnabled) return@collectLatest
            val wsFlow = emptyFlow<Result<Long>>()         // todo websocket, we keep a emptyflow placeholder for now
            val pollFlow = flow {
                while (true) {
                    delay(POLL_MS)
                    (getBalanceEvmHttp(publicKey,evmChainId) as? Result.Success)?.data?.let { emit(Result.Success(it)) }
                }
            }
            merge(wsFlow, pollFlow).collect { result ->
                if (result.isSuccess() && result.data != lastAmount) {
                    lastAmount = result.data
                    send(result)
                }
            }
        }

    }.flowOn(Dispatchers.IO)

    private suspend fun getBalanceEvmHttp(publicKey: String, evmChainId: EvmChain): Result<Long> {
        val url = evmHttpResolver.resolveEvmUrl(evmChainId)
        val response: Rpc20Response<String> = RpcRequestFactory.makeRpcRequest(
            url = url.first,
            request = getEvmBalanceRequest(publicKey),
            resultSerializer = String.serializer()
        )
        return if (response.error != null) {
            Result.Failure("${response.error?.code}, ${response.error?.message}")
        } else {
            val hexBalance = response.result
            val balance = try {
                if (!hexBalance.isNullOrEmpty()) {
                    java.lang.Long.decode(hexBalance)
                } else {
                    0L
                }
            } catch (e: Exception) {
                0L
            }
            Result.Success(balance)
        }
    }

}