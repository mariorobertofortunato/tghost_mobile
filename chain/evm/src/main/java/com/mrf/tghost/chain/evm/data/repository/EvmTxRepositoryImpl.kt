package com.mrf.tghost.chain.evm.data.repository

import com.mrf.tghost.chain.evm.data.network.http.rpcrequests.getAlchemyEvmAssetTransfersFromRequest
import com.mrf.tghost.chain.evm.data.network.http.rpcrequests.getAlchemyEvmAssetTransfersToRequest
import com.mrf.tghost.chain.evm.data.network.mappers.normalizeEvmTransactions
import com.mrf.tghost.chain.evm.data.network.mappers.toDomainModel
import com.mrf.tghost.chain.evm.data.network.mappers.toDomainTransactionsFromAlchemy
import com.mrf.tghost.chain.evm.data.network.model.alchemy.AlchemyAssetTransferDto
import com.mrf.tghost.chain.evm.data.network.model.alchemy.AlchemyAssetTransfersResponseDto
import com.mrf.tghost.chain.evm.data.network.model.moralis.MoralisActivityResponseDto
import com.mrf.tghost.chain.evm.data.network.pagination.AlchemyCursorPage
import com.mrf.tghost.chain.evm.data.network.pagination.paginateAlchemyByCursor
import com.mrf.tghost.chain.evm.data.network.resolver.http.EvmHttpResolver
import com.mrf.tghost.chain.evm.domain.repository.EvmTxRepository
import com.mrf.tghost.chain.evm.utils.ALCHEMY_API_URL
import com.mrf.tghost.chain.evm.utils.MORALIS_API_URL
import com.mrf.tghost.data.network.client.KtorClient
import com.mrf.tghost.data.network.http.factory.RpcRequestFactory
import com.mrf.tghost.domain.model.EvmChain
import com.mrf.tghost.domain.model.Result
import com.mrf.tghost.domain.model.Transaction
import com.solana.rpccore.Rpc20Response
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EvmTxRepositoryImpl @Inject constructor(
    private val evmHttpResolver: EvmHttpResolver,
) : EvmTxRepository {

    override fun txEvm(publicKey: String, chainId: EvmChain?
    ): Flow<Result<List<Transaction>>?> = flow {
        val httpResult = getEvmTxHttp(publicKey, chainId)
        emit(httpResult)
        // todo websocket/polling etc
    }.flowOn(Dispatchers.IO)

    private suspend fun getEvmTxHttp(address: String, chainId: EvmChain?): Result<List<Transaction>> =
        withContext(Dispatchers.IO) {
            try {
                val baseUrl = evmHttpResolver.resolveEvmUrl(chainId)
                when (baseUrl.first) {
                    MORALIS_API_URL -> {
                        val moralisChain = chainId?.chain ?: "eth"
                        val url = "${MORALIS_API_URL}/wallets/$address/history?chain=${moralisChain}&order=DESC"
                        val response = KtorClient.httpClient.get(url) {
                            header("X-API-Key", baseUrl.second?.trim().orEmpty())
                        }.body<MoralisActivityResponseDto>()
                        Result.Success(
                            response.toDomainModel(
                                accountAddress = address,
                                chainId = chainId ?: EvmChain.ETHEREUM,
                            ).normalizeEvmTransactions()
                        )
                    }
                    ALCHEMY_API_URL -> {
                        val chainsToQuery = chainId?.let(::listOf) ?: EvmChain.entries
                        val allTransactions = mutableListOf<Transaction>()
                        chainsToQuery.forEach { evmChain ->
                            val rpcUrl = evmHttpResolver.resolveAlchemyRpcUrl(evmChain)
                            val includeInternal = evmChain == EvmChain.ETHEREUM
                            val outgoingTransfers = fetchAlchemyTransfers(
                                rpcUrl = rpcUrl,
                                address = address,
                                outgoing = true,
                                includeInternal = includeInternal,
                            )
                            val incomingTransfers = fetchAlchemyTransfers(
                                rpcUrl = rpcUrl,
                                address = address,
                                outgoing = false,
                                includeInternal = includeInternal,
                            )
                            val chainTransfers = outgoingTransfers + incomingTransfers
                            allTransactions += chainTransfers.toDomainTransactionsFromAlchemy(
                                accountAddress = address,
                                chainId = evmChain,
                            )
                        }
                        Result.Success(allTransactions.normalizeEvmTransactions())
                    }
                    else -> {
                        Result.Success(emptyList())
                    }
                }

            } catch (e: Exception) {
                Result.Failure(e.message ?: "Unknown error fetching EVM transactions")
            }

        }

    private suspend fun fetchAlchemyTransfers(
        rpcUrl: String,
        address: String,
        outgoing: Boolean,
        includeInternal: Boolean,
    ): List<AlchemyAssetTransferDto> {
        return paginateAlchemyByCursor { pageKey ->
            val request = if (outgoing) {
                getAlchemyEvmAssetTransfersFromRequest(
                    address = address,
                    includeInternal = includeInternal,
                    pageKey = pageKey,
                )
            } else {
                getAlchemyEvmAssetTransfersToRequest(
                    address = address,
                    includeInternal = includeInternal,
                    pageKey = pageKey,
                )
            }
            val response: Rpc20Response<AlchemyAssetTransfersResponseDto> = RpcRequestFactory.makeRpcRequest(
                url = rpcUrl,
                request = request,
                resultSerializer = AlchemyAssetTransfersResponseDto.serializer(),
            )
            AlchemyCursorPage(
                items = response.result?.transfers.orEmpty(),
                nextCursor = response.result?.pageKey,
            )
        }
    }
}