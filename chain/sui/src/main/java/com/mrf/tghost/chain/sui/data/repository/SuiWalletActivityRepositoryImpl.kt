package com.mrf.tghost.chain.sui.data.repository

import com.mrf.tghost.chain.sui.data.network.http.rpcrequests.getSuiWalletActivityGraphQlRequest
import com.mrf.tghost.chain.sui.data.network.mappers.toDomainTransactions
import com.mrf.tghost.chain.sui.data.network.model.SuiTransactionNodeGraphQlDto
import com.mrf.tghost.chain.sui.data.network.model.SuiWalletActivityGraphQlDataDto
import com.mrf.tghost.chain.sui.data.network.resolver.http.SuiHttpResolver
import com.mrf.tghost.chain.sui.domain.repository.SuiWalletActivityRepository
import com.mrf.tghost.chain.sui.utils.POLL_MS
import com.mrf.tghost.data.datastore.DataStore
import com.mrf.tghost.data.network.http.factory.GraphQlRequestFactory
import com.mrf.tghost.domain.model.Result
import com.mrf.tghost.domain.model.Transaction
import com.mrf.tghost.domain.model.isSuccess
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

class SuiWalletActivityRepositoryImpl @Inject constructor(
    private val suiHttpResolver: SuiHttpResolver,
    private val dataStore: DataStore,
) : SuiWalletActivityRepository {

    override fun suiWalletActivity(walletAddress: String): Flow<Result<List<Transaction>>?> = channelFlow {
        val httpResult = loadWalletActivityPages(walletAddress)
        send(httpResult)
        var lastList: List<Transaction>? = (httpResult as? Result.Success)?.data

        dataStore.getLiveUpdateStatus.collectLatest { liveUpdateEnabled ->
            if (!liveUpdateEnabled) return@collectLatest
            val wsFlow = emptyFlow<Result<List<Transaction>>>() // todo websocket, placeholder for now
            val pollFlow = flow {
                while (true) {
                    delay(POLL_MS)
                    (loadWalletActivityPages(walletAddress) as? Result.Success)?.let { emit(it) }
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

    private suspend fun loadWalletActivityPages(walletAddress: String): Result<List<Transaction>> {
        var cursor: String? = null
        var pageCount = 0
        val all = mutableListOf<SuiTransactionNodeGraphQlDto>()

        do {
            pageCount += 1
            val response = GraphQlRequestFactory.makeGraphQlRequest(
                url = suiHttpResolver.resolveSuiGraphQlUrl(),
                request = getSuiWalletActivityGraphQlRequest(
                    walletAddress = walletAddress,
                    afterCursor = cursor
                ),
                dataSerializer = SuiWalletActivityGraphQlDataDto.serializer()
            )

            val errors = response.errors
            if (!errors.isNullOrEmpty()) {
                return Result.Failure(errors.joinToString("; ") { it.message })
            }

            val connection = response.data?.transactions
            val nodes = connection?.nodes.orEmpty()
            all.addAll(nodes)

            val pageInfo = connection?.pageInfo
            val hasNextPage = pageInfo?.hasNextPage == true
            val endCursor = pageInfo?.endCursor
            cursor = if (hasNextPage) endCursor else null
        } while (cursor != null && pageCount < 20)

        return Result.Success(all.toDomainTransactions())
    }
}
