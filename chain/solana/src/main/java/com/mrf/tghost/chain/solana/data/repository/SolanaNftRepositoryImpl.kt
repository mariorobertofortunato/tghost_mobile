package com.mrf.tghost.chain.solana.data.repository

import com.mrf.tghost.chain.solana.data.network.http.rpcrequests.getAssetsByOwnerRequest
import com.mrf.tghost.chain.solana.data.network.mappers.toDomainModel
import com.mrf.tghost.chain.solana.data.network.model.DasApiResponseDto
import com.mrf.tghost.chain.solana.data.network.model.DasResultDto
import com.mrf.tghost.chain.solana.data.network.resolver.http.SolanaHttpResolver
import com.mrf.tghost.chain.solana.domain.model.DasApiResponse
import com.mrf.tghost.chain.solana.domain.network.SolanaWsCoordinator
import com.mrf.tghost.chain.solana.domain.repository.SolanaNftRepository
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
import javax.inject.Inject

class SolanaNftRepositoryImpl @Inject constructor(
    private val solanaWsCoordinator: SolanaWsCoordinator,
    private val solanaHttpResolver: SolanaHttpResolver,
    private val dataStore: DataStore
) : SolanaNftRepository {

    override fun solanaNftAccounts(publicKey: String): Flow<Result<DasApiResponse>> = channelFlow {
        val httpResult = getSolanaNftAccountsHttp(publicKey)
        send(httpResult)
        var lastResponse: DasApiResponse? = (httpResult as? Result.Success)?.data

        dataStore.getLiveUpdateStatus.collectLatest { liveUpdateEnabled ->
            if (!liveUpdateEnabled) return@collectLatest
            val wsFlow = solanaWsCoordinator.subscribeNftAccounts(publicKey)
            val pollFlow = flow {
                while (true) {
                    delay(POLL_MS)
                    (getSolanaNftAccountsHttp(publicKey)  as? Result.Success)?.let { emit(it) }
                }
            }
            merge(wsFlow, pollFlow).collect { result ->
                if (result.isSuccess() && result.data != lastResponse) {
                    lastResponse = result.data
                    send(result)
                }
            }
        }
    }.flowOn(Dispatchers.IO)

    private suspend fun getSolanaNftAccountsHttp(publicKey: String): Result<DasApiResponse> {
            val url = solanaHttpResolver.resolveSolanaUrl()
            val response: Rpc20Response<DasResultDto> =
                RpcRequestFactory.makeRpcRequest(
                    url = url,
                    request = getAssetsByOwnerRequest(publicKey),
                    resultSerializer = DasResultDto.serializer()
                )
            return if (response.error != null) {
                Result.Failure("${response.error?.code}, ${response.error?.message}")
            } else {
                Result.Success(DasApiResponseDto(result = response.result).toDomainModel())
            }
        }
}