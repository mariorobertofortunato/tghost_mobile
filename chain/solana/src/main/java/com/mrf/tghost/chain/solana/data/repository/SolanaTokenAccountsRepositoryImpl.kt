package com.mrf.tghost.chain.solana.data.repository

import com.mrf.tghost.chain.solana.data.network.http.rpcrequests.getSolanaTokenAccountsRequest
import com.mrf.tghost.chain.solana.data.network.mappers.toDomainModel
import com.mrf.tghost.chain.solana.data.network.model.SolanaRpcResponseDto
import com.mrf.tghost.chain.solana.data.network.model.SolanaSplTokenAccountDto
import com.mrf.tghost.chain.solana.data.network.resolver.http.SolanaHttpResolver
import com.mrf.tghost.chain.solana.domain.model.SolanaSplTokenAccount
import com.mrf.tghost.chain.solana.domain.network.SolanaWsCoordinator
import com.mrf.tghost.chain.solana.domain.repository.SolanaTokenAccountsRepository
import com.mrf.tghost.chain.solana.utils.POLL_MS
import com.mrf.tghost.chain.solana.utils.SOLANA_SPL_TOKEN_PROGRAM_ID
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
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.nullable
import javax.inject.Inject

class SolanaTokenAccountsRepositoryImpl @Inject constructor(
    private val solanaWsCoordinator: SolanaWsCoordinator,
    private val solanaHttpResolver: SolanaHttpResolver,
    private val dataStore: DataStore
) : SolanaTokenAccountsRepository {

    override fun solanaTokenAccounts(publicKey: String): Flow<Result<List<SolanaSplTokenAccount>>> =
        channelFlow {
            val httpResult = getSolanaTokenAccountsHttp(publicKey)
            send(httpResult)
            var lastList: List<SolanaSplTokenAccount>? = (httpResult as? Result.Success)?.data

            dataStore.getLiveUpdateStatus.collectLatest { liveUpdateEnabled ->
                if (!liveUpdateEnabled) return@collectLatest
                val wsFlow = solanaWsCoordinator.subscribeTokenAccounts(publicKey)
                val pollFlow = flow {
                    while (true) {
                        delay(POLL_MS)
                        (getSolanaTokenAccountsHttp(publicKey) as? Result.Success)?.let { emit(it) }
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

    private suspend fun getSolanaTokenAccountsHttp(publicKey: String): Result<List<SolanaSplTokenAccount>> {
            val url = solanaHttpResolver.resolveSolanaUrl()
            val response: Rpc20Response<SolanaRpcResponseDto<List<SolanaSplTokenAccountDto>?>> =
                RpcRequestFactory.makeRpcRequest(
                    url = url,
                    request = getSolanaTokenAccountsRequest(
                        publicKey,
                        SOLANA_SPL_TOKEN_PROGRAM_ID
                    ),
                    resultSerializer = SolanaRpcResponseDto.serializer(
                        ListSerializer(
                            SolanaSplTokenAccountDto.serializer()
                        ).nullable
                    )
                )
            return if (response.error != null) {
                Result.Failure("${response.error?.code}, ${response.error?.message}")
            } else {
                response.result?.value?.let {
                    Result.Success(it.map { dto -> dto.toDomainModel() })
                } ?: Result.Success(emptyList())
            }
        }

}
