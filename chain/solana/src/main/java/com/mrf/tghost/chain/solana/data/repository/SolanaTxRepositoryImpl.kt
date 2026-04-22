package com.mrf.tghost.chain.solana.data.repository

import com.mrf.tghost.chain.solana.data.network.http.rpcrequests.getSolanaSignaturesForAddress
import com.mrf.tghost.chain.solana.data.network.http.rpcrequests.getSolanaTransactionRequest
import com.mrf.tghost.chain.solana.data.network.mappers.toDomainModel
import com.mrf.tghost.chain.solana.data.network.model.SolanaSignatureDto
import com.mrf.tghost.chain.solana.data.network.model.SolanaTransactionDto
import com.mrf.tghost.chain.solana.data.network.resolver.http.SolanaHttpResolver
import com.mrf.tghost.chain.solana.domain.network.SolanaWsCoordinator
import com.mrf.tghost.chain.solana.domain.repository.SolanaTxRepository
import com.mrf.tghost.data.datastore.DataStore
import com.mrf.tghost.data.network.http.factory.RpcRequestFactory
import com.mrf.tghost.domain.model.Result
import com.mrf.tghost.domain.model.Transaction
import com.solana.rpccore.Rpc20Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.ListSerializer
import javax.inject.Inject

class SolanaTxRepositoryImpl @Inject constructor(
    private val solanaWsCoordinator: SolanaWsCoordinator,
    private val solanaHttpResolver: SolanaHttpResolver,
    private val dataStore: DataStore
) : SolanaTxRepository {
    override fun txSolana(publicKey: String): Flow<Result<List<Transaction>>?> = channelFlow {
        val httpResult = getSolanaTxHttp(publicKey)
        send(httpResult)

        // TODO Websocket
    }.flowOn(Dispatchers.IO)

    private suspend fun getSolanaTxHttp(publicKey: String): Result<List<Transaction>> =
        withContext(Dispatchers.IO) {
            val url = solanaHttpResolver.resolveSolanaUrl()
            val signatureDtos = fetchSignaturesPaged(url = url, publicKey = publicKey)
            if (signatureDtos is Result.Failure) return@withContext signatureDtos

            val signatureList = (signatureDtos as Result.Success).data
            if (signatureList.isEmpty()) return@withContext Result.Success(emptyList())

            val semaphore = Semaphore(MAX_CONCURRENT_TX_REQUESTS)
            val transactions = coroutineScope {
                signatureList.map { sigDto ->
                    async {
                        semaphore.withPermit {
                            val txResponse: Rpc20Response<SolanaTransactionDto> = RpcRequestFactory.makeRpcRequest(
                                url = url,
                                request = getSolanaTransactionRequest(sigDto.signature),
                                resultSerializer = SolanaTransactionDto.serializer()
                            )
                            if (txResponse.error != null) return@withPermit null
                            txResponse.result?.toDomainModel(
                                signature = sigDto.signature,
                                walletAddress = publicKey
                            )
                        }
                    }
                }.awaitAll()
            }.filterNotNull()
                .filterNot { it.balanceChanges.isEmpty() || isFeeOnlyTransaction(it) }
                .distinctBy { it.id }
                .sortedByDescending { it.timestamp ?: Long.MIN_VALUE }

            Result.Success(transactions)
        }

    private suspend fun fetchSignaturesPaged(
        url: String,
        publicKey: String
    ): Result<List<SolanaSignatureDto>> {
        val signatureDtos = mutableListOf<SolanaSignatureDto>()
        var before: String? = null

        while (signatureDtos.size < MAX_SIGNATURES_TO_FETCH) {
            val remaining = MAX_SIGNATURES_TO_FETCH - signatureDtos.size
            val pageLimit = minOf(SIGNATURES_PAGE_SIZE, remaining)
            val signaturesResponse: Rpc20Response<List<SolanaSignatureDto>> =
                RpcRequestFactory.makeRpcRequest(
                    url = url,
                    request = getSolanaSignaturesForAddress(
                        walletAddress = publicKey,
                        limit = pageLimit,
                        before = before
                    ),
                    resultSerializer = ListSerializer(SolanaSignatureDto.serializer())
                )

            if (signaturesResponse.error != null) {
                return Result.Failure("${signaturesResponse.error?.code}, ${signaturesResponse.error?.message}")
            }

            val page = signaturesResponse.result.orEmpty()
            if (page.isEmpty()) break

            signatureDtos += page
            before = page.lastOrNull()?.signature
            if (page.size < pageLimit || before.isNullOrBlank()) break
        }

        return Result.Success(signatureDtos)
    }

    private fun isFeeOnlyTransaction(transaction: Transaction): Boolean {
        if (transaction.balanceChanges.size != 1) return false
        val onlyChange = transaction.balanceChanges.first()
        if (!onlyChange.isNative || onlyChange.amount >= 0L) return false
        val fee = transaction.fee ?: return false
        val charged = -onlyChange.amount
        return charged <= fee + FEE_NOISE_LAMPORTS
    }

    companion object {
        private const val SIGNATURES_PAGE_SIZE = 50
        private const val MAX_SIGNATURES_TO_FETCH = 1000
        private const val MAX_CONCURRENT_TX_REQUESTS = 8
        private const val FEE_NOISE_LAMPORTS = 10_000L
    }
}
