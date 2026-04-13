package com.mrf.tghost.chain.tezos.data.repository

import com.mrf.tghost.data.network.client.KtorClient
import com.mrf.tghost.chain.tezos.data.network.mappers.toDomainModel
import com.mrf.tghost.chain.tezos.data.network.model.TezosTokenDto
import com.mrf.tghost.chain.tezos.domain.model.TezosToken
import com.mrf.tghost.chain.tezos.domain.repository.TezosTokenAccountsRepository
import com.mrf.tghost.chain.tezos.utils.POLL_MS
import com.mrf.tghost.chain.tezos.utils.TZKT_API_URL
import com.mrf.tghost.data.datastore.DataStore
import com.mrf.tghost.domain.model.Result
import com.mrf.tghost.domain.model.isSuccess
import io.ktor.client.call.body
import io.ktor.client.request.get
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

class TezosTokenAccountsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore
) : TezosTokenAccountsRepository {

    override fun tezosTokenAccounts(publicKey: String): Flow<Result<List<TezosToken>>?> = channelFlow {
        val httpResult = getTezosTokenAccountsHttp(publicKey)
        send(httpResult)
        var lastList: List<TezosToken>? = (httpResult as? Result.Success)?.data

        dataStore.getLiveUpdateStatus.collectLatest { liveUpdateEnabled ->
            if (!liveUpdateEnabled) return@collectLatest
            val wsFlow = emptyFlow<Result<List<TezosToken>>>()         // todo websocket, we keep a emptyflow placeholder for now
            val pollFlow = flow {
                while (true) {
                    delay(POLL_MS)
                    (getTezosTokenAccountsHttp(publicKey) as? Result.Success)?.let { emit(it) }
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

    private suspend fun getTezosTokenAccountsHttp(publicKey: String):Result<List<TezosToken>>  {
        //val url = tezosHttpResolver.resolveTezosUrl()
        try {
            val url = "${TZKT_API_URL}/tokens/balances?account=$publicKey&balance.gt=0&limit=10000"
            val response = KtorClient.httpClient.get(url).body<List<TezosTokenDto>>()
            return Result.Success(response.map { it.toDomainModel() })
        } catch (e: Exception) {
            return Result.Failure(e.message ?: "Error fetching Tezos tokens")
        }

    }
}
