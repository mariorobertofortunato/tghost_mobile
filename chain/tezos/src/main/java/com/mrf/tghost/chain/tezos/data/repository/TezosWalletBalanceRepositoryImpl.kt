package com.mrf.tghost.chain.tezos.data.repository

import com.mrf.tghost.chain.tezos.domain.repository.TezosWalletBalanceRepository
import com.mrf.tghost.chain.tezos.utils.POLL_MS
import com.mrf.tghost.chain.tezos.utils.TEZOS_MAINNET_RPC_URL
import com.mrf.tghost.data.datastore.DataStore
import com.mrf.tghost.data.network.client.KtorClient
import com.mrf.tghost.domain.model.Result
import com.mrf.tghost.domain.model.isSuccess
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.merge
import javax.inject.Inject

private const val TAG = "WalletBalanceRepo"

class TezosWalletBalanceRepositoryImpl @Inject constructor(
    private val dataStore: DataStore
) : TezosWalletBalanceRepository {

    override fun balanceTezos(publicKey: String): Flow<Result<Long>?> = channelFlow {
        val httpResult = getBalanceTezosHttp(publicKey)
        send(httpResult)
        var lastAmount: Long? = (httpResult as? Result.Success)?.data

        dataStore.getLiveUpdateStatus.collectLatest { liveUpdateEnabled ->
            if (!liveUpdateEnabled) return@collectLatest
            val wsFlow = emptyFlow<Result<Long>>()         // todo websocket, we keep a emptyflow placeholder for now
            val pollFlow = flow {
                while (true) {
                    delay(POLL_MS)
                    (getBalanceTezosHttp(publicKey) as? Result.Success)?.data?.let { emit(Result.Success(it)) }
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

    private suspend fun getBalanceTezosHttp(publicKey: String) : Result<Long> {
        //val url = tezosHttpResolver.resolveTezosUrl()
        try {
            // We need to fetch from the rpc in order to get the native xtz balance only (the api.tzkt returns the overall balance, consolidated with baking positions)
            val url = "${TEZOS_MAINNET_RPC_URL}chains/main/blocks/head/context/contracts/$publicKey/balance"
            val response = KtorClient.httpClient.get(url).bodyAsText()
            val balance = response.replace("\"", "").trim().toLong()
            return Result.Success(balance)
        } catch (e: Exception) {
            return Result.Failure(e.message ?: "Error fetching Tezos balance")
        }
    }

}