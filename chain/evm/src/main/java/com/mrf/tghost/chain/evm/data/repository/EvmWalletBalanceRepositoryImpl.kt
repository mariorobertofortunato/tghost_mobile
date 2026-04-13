package com.mrf.tghost.chain.evm.data.repository

import com.mrf.tghost.data.network.http.factory.RpcRequestFactory
import com.mrf.tghost.domain.model.EvmChain
import com.mrf.tghost.domain.model.Result
import com.mrf.tghost.chain.evm.data.network.http.rpcrequests.getEvmBalanceRequest
import com.mrf.tghost.chain.evm.data.network.resolver.http.EvmHttpResolver
import com.mrf.tghost.chain.evm.domain.repository.EvmWalletBalanceRepository
import com.solana.rpccore.Rpc20Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.builtins.serializer
import javax.inject.Inject

class EvmWalletBalanceRepositoryImpl @Inject constructor(
    private val evmHttpResolver: EvmHttpResolver
) : EvmWalletBalanceRepository {

    override fun balanceEvm(publicKey: String, evmChainId: EvmChain): Flow<Result<Long>?> = flow {
        val httpResult = getBalanceEvmHttp(publicKey, evmChainId)
        emit(httpResult)
        // todo websocket, fallback polling etc (see solana)
    }.flowOn(Dispatchers.IO)

    private suspend fun getBalanceEvmHttp(publicKey: String, evmChainId: EvmChain): Result<Long> {
        val url = evmHttpResolver.resolveEvmUrl(evmChainId)
        val response: Rpc20Response<String> = RpcRequestFactory.makeRpcRequest(
            url = url,
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