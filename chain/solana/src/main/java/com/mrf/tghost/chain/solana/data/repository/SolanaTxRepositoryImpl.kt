package com.mrf.tghost.chain.solana.data.repository

import com.mrf.tghost.chain.solana.data.network.http.rpcrequests.getSolanaSignaturesForAddress
import com.mrf.tghost.chain.solana.data.network.http.rpcrequests.getSolanaTransactionRequest
import com.mrf.tghost.chain.solana.data.network.mappers.toDomainModel
import com.mrf.tghost.chain.solana.data.network.model.SolanaSignatureDto
import com.mrf.tghost.chain.solana.data.network.model.SolanaTransactionDto
import com.mrf.tghost.chain.solana.data.network.resolver.http.SolanaHttpResolver
import com.mrf.tghost.chain.solana.domain.network.SolanaWsCoordinator
import com.mrf.tghost.chain.solana.domain.repository.SolanaTxRepository
import com.mrf.tghost.data.network.http.factory.RpcRequestFactory
import com.mrf.tghost.domain.model.Result
import com.mrf.tghost.domain.model.Transaction
import com.solana.rpccore.Rpc20Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.ListSerializer
import javax.inject.Inject

class SolanaTxRepositoryImpl @Inject constructor(
    private val solanaWsCoordinator: SolanaWsCoordinator,
    private val solanaHttpResolver: SolanaHttpResolver
) : SolanaTxRepository {

    override fun txSolana(publicKey: String): Flow<Result<List<Transaction>>?> = flow {
        val httpResult = getSolanaTxHttp(publicKey)
        emit(httpResult)
        // TODO websocket
    }.flowOn(Dispatchers.IO)

    private suspend fun getSolanaTxHttp(publicKey: String): Result<List<Transaction>> =
        withContext(Dispatchers.IO) {
            val url = solanaHttpResolver.resolveSolanaUrl()
            val signaturesResponse: Rpc20Response<List<SolanaSignatureDto>> =
                RpcRequestFactory.makeRpcRequest(
                    url = url,
                    request = getSolanaSignaturesForAddress(publicKey),
                    resultSerializer = ListSerializer(SolanaSignatureDto.serializer())
                )

            if (signaturesResponse.error != null) {
                return@withContext Result.Failure("${signaturesResponse.error?.code}, ${signaturesResponse.error?.message}")
            }

            val signatureDtos = signaturesResponse.result ?: return@withContext Result.Success(emptyList())

            val transactions = signatureDtos.map { sigDto ->
                async {
                    val txResponse: Rpc20Response<SolanaTransactionDto> = RpcRequestFactory.makeRpcRequest(
                        url = url,
                        request = getSolanaTransactionRequest(sigDto.signature),
                        resultSerializer = SolanaTransactionDto.serializer()
                    )
                    txResponse.result?.toDomainModel(sigDto.signature)
                }
            }.awaitAll().filterNotNull()

            Result.Success(transactions)
        }
}
