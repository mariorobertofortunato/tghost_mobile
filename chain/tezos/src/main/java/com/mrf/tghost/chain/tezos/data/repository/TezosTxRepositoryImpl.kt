package com.mrf.tghost.chain.tezos.data.repository

import com.mrf.tghost.chain.tezos.data.network.mappers.toDomainModel
import com.mrf.tghost.chain.tezos.data.network.model.TezosActivityDto
import com.mrf.tghost.chain.tezos.domain.repository.TezosTxRepository
import com.mrf.tghost.chain.tezos.utils.POLL_MS
import com.mrf.tghost.chain.tezos.utils.TZKT_API_URL
import com.mrf.tghost.data.datastore.DataStore
import com.mrf.tghost.data.network.client.KtorClient
import com.mrf.tghost.domain.model.Result
import com.mrf.tghost.domain.model.Transaction
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

class TezosTxRepositoryImpl @Inject constructor(
    private val dataStore: DataStore
): TezosTxRepository {

    override fun txTezos(publicKey: String): Flow<Result<List<Transaction>>?> = channelFlow {
        val httpResult = getTezosTxHttp(publicKey)
        send(httpResult)
        var lastList: List<Transaction>? = (httpResult as? Result.Success)?.data

        dataStore.getLiveUpdateStatus.collectLatest { liveUpdateEnabled ->
            if (!liveUpdateEnabled) return@collectLatest
            val wsFlow = emptyFlow<Result<List<Transaction>>>()         // todo websocket, we keep a emptyflow placeholder for now
            val pollFlow = flow {
                while (true) {
                    delay(POLL_MS)
                    (getTezosTxHttp(publicKey) as? Result.Success)?.let { emit(it) }
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

    private suspend fun getTezosTxHttp(publicKey: String): Result<List<Transaction>> {
        //val url = tezosHttpResolver.resolveTezosUrl()
        try {
            val url = "${TZKT_API_URL}/accounts/activity?addresses=$publicKey"
            val response = KtorClient.httpClient.get(url).body<List<TezosActivityDto>>()
            return Result.Success(response.map { it.toDomainModel(publicKey) })
        } catch (e: Exception) {
            return Result.Failure(e.message ?: "Error fetching Tezos tokens")
        }
    }


}