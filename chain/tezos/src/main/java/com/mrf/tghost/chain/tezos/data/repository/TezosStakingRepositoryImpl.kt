package com.mrf.tghost.chain.tezos.data.repository

import com.mrf.tghost.chain.tezos.domain.model.TezosAccountDelegation
import com.mrf.tghost.chain.tezos.domain.repository.TezosStakingRepository
import com.mrf.tghost.chain.tezos.utils.POLL_MS
import com.mrf.tghost.chain.tezos.utils.TZKT_API_URL
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
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import javax.inject.Inject

class TezosStakingRepositoryImpl @Inject constructor(
    private val dataStore: DataStore
) : TezosStakingRepository {

    override fun tezosStakingAccounts(publicKey: String): Flow<Result<List<TezosAccountDelegation>>?> = channelFlow {
        val httpResult = getTezosStakingAccountsHttp(publicKey)
        send(httpResult)
        var lastList: List<TezosAccountDelegation>? = (httpResult as? Result.Success)?.data

        dataStore.getLiveUpdateStatus.collectLatest { liveUpdateEnabled ->
            if (!liveUpdateEnabled) return@collectLatest
            val wsFlow = emptyFlow<Result<List<TezosAccountDelegation>>>()         // todo websocket, we keep a emptyflow placeholder for now
            val pollFlow = flow {
                while (true) {
                    delay(POLL_MS)
                    (getTezosStakingAccountsHttp(publicKey) as? Result.Success)?.let { emit(it) }
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

    private suspend fun getTezosStakingAccountsHttp(publicKey: String): Result<List<TezosAccountDelegation>> {
        try {
            val url = "${TZKT_API_URL}/accounts/$publicKey"
            val response = KtorClient.httpClient.get(url).bodyAsText()
            val json = Json.Default
                .parseToJsonElement(response)
                .jsonObject

            val delegate = json["delegate"]?.jsonObject
            if (delegate?.isEmpty() == true) {
                return Result.Success(emptyList())
            }
            val bakerAlias = delegate?.get("alias")?.jsonPrimitive?.content ?: "Unknown Baker"
            val bakerAddress = delegate?.get("address")?.jsonPrimitive?.content ?: ""
            val delegationStatus = delegate?.get("active")?.jsonPrimitive?.content ?: ""
            val stakedBalance = json["stakedBalance"]?.jsonPrimitive?.longOrNull ?: 0L

            if (stakedBalance <= 0L) {
                return Result.Success(emptyList())
            }

            val stakeAccountResponse = TezosAccountDelegation(
                bakerAlias = bakerAlias,
                bakerAddress = bakerAddress,
                amount = stakedBalance.toBigDecimal(),
                active = delegationStatus,
            )
            return Result.Success(listOf(stakeAccountResponse))
        } catch (e: Exception) {
            return Result.Failure(e.message ?: "Error fetching Tezos staking accounts")
        }
    }
}