package com.mrf.tghost.chain.sui.data.repository

import com.mrf.tghost.chain.sui.data.network.http.rpcrequests.getSuiOwnedObjectsGraphQlRequest
import com.mrf.tghost.chain.sui.data.network.mappers.toDomainModel
import com.mrf.tghost.chain.sui.data.network.model.SuiOwnedObjectsGraphQlDataDto
import com.mrf.tghost.chain.sui.data.network.resolver.http.SuiHttpResolver
import com.mrf.tghost.chain.sui.domain.model.SuiObject
import com.mrf.tghost.chain.sui.domain.model.SuiOwnedObjects
import com.mrf.tghost.chain.sui.domain.repository.SuiOwnedObjectsRepository
import com.mrf.tghost.chain.sui.utils.POLL_MS
import com.mrf.tghost.data.datastore.DataStore
import com.mrf.tghost.data.network.http.factory.GraphQlRequestFactory
import com.mrf.tghost.domain.model.Result
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

class SuiOwnedObjectsRepositoryImpl @Inject constructor(
    private val suiHttpResolver: SuiHttpResolver,
    private val dataStore: DataStore,
) : SuiOwnedObjectsRepository {

    override fun suiOwnedObjects(publicKey: String): Flow<Result<SuiOwnedObjects>?> = channelFlow {
        val httpResult = getSuiOwnedObjectsGraphQl(publicKey)
        send(httpResult)
        var lastValue: SuiOwnedObjects? = (httpResult as? Result.Success)?.data

        dataStore.getLiveUpdateStatus.collectLatest { liveUpdateEnabled ->
            if (!liveUpdateEnabled) return@collectLatest
            val wsFlow = emptyFlow<Result<SuiOwnedObjects>>() // todo websocket, placeholder for now
            val pollFlow = flow {
                while (true) {
                    delay(POLL_MS)
                    (getSuiOwnedObjectsGraphQl(publicKey) as? Result.Success)?.let { emit(it) }
                }
            }
            merge(wsFlow, pollFlow).collect { result ->
                if (result.isSuccess() && result.data != lastValue) {
                    lastValue = result.data
                    send(result)
                }
            }
        }
    }.flowOn(Dispatchers.IO)

    private suspend fun getSuiOwnedObjectsGraphQl(publicKey: String): Result<SuiOwnedObjects> {
        var cursor: String? = null
        var pageCount = 0
        val allObjects = mutableListOf<SuiObject>()
        var hasNextPage: Boolean
        var endCursor: String?

        do {
            pageCount += 1
            val response = GraphQlRequestFactory.makeGraphQlRequest(
                url = suiHttpResolver.resolveSuiGraphQlUrl(),
                request = getSuiOwnedObjectsGraphQlRequest(
                    address = publicKey,
                    afterCursor = cursor
                ),
                dataSerializer = SuiOwnedObjectsGraphQlDataDto.serializer()
            )

            val errors = response.errors
            if (!errors.isNullOrEmpty()) {
                val errorMessage = errors.joinToString("; ") { it.message }
                return Result.Failure(errorMessage)
            }

            val connection = response.data?.address?.objects
            val pageResult = connection?.toDomainModel()
            allObjects.addAll(pageResult?.data.orEmpty())

            hasNextPage = pageResult?.hasNextPage == true
            endCursor = pageResult?.nextCursor
            cursor = if (hasNextPage) endCursor else null
        } while (cursor != null && pageCount < 20)

        return Result.Success(
            SuiOwnedObjects(
                data = allObjects,
                nextCursor = endCursor,
                hasNextPage = hasNextPage
            )
        )
    }
}
