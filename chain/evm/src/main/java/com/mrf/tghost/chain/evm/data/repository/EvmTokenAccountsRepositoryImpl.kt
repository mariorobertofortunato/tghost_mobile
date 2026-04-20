package com.mrf.tghost.chain.evm.data.repository

import com.mrf.tghost.chain.evm.data.network.mappers.toDomainModelFromAlchemy
import com.mrf.tghost.chain.evm.data.network.mappers.toEvmTokenAccounts
import com.mrf.tghost.chain.evm.data.network.model.alchemy.AlchemyTokensByAddressResponseDto
import com.mrf.tghost.chain.evm.data.network.pagination.AlchemyCursorPage
import com.mrf.tghost.chain.evm.data.network.pagination.paginateAlchemyByCursor
import com.mrf.tghost.chain.evm.data.network.model.moralis.MoralisWalletTokensResponseDto
import com.mrf.tghost.chain.evm.data.network.resolver.http.EvmHttpResolver
import com.mrf.tghost.chain.evm.domain.model.EvmTokenAccount
import com.mrf.tghost.chain.evm.domain.repository.EvmTokenAccountsRepository
import com.mrf.tghost.chain.evm.utils.ALCHEMY_API_URL
import com.mrf.tghost.chain.evm.utils.MORALIS_API_URL
import com.mrf.tghost.data.network.client.KtorClient
import com.mrf.tghost.domain.model.EvmChain
import com.mrf.tghost.domain.model.Result
import com.mrf.tghost.domain.model.toAlchemyNetwork
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import javax.inject.Inject

class EvmTokenAccountsRepositoryImpl @Inject constructor(
    private val evmHttpResolver: EvmHttpResolver,
) : EvmTokenAccountsRepository {

    @Serializable
    private data class AlchemyAddressRequest(
        val address: String,
        val networks: List<String>,
    )

    @Serializable
    private data class AlchemyTokenAccountsRequest(
        val addresses: List<AlchemyAddressRequest>,
        val withMetadata: Boolean = true,
        val includeNativeTokens: Boolean = true,
        val includeErc20Tokens: Boolean = true,
        val pageKey: String? = null,
    )

    override fun evmTokenAccounts(publicKey: String, chainId: EvmChain?): Flow<Result<List<EvmTokenAccount>>?> = flow {
        val httpResult = getEvmTokenAccountsHttp(publicKey, chainId)
        emit(httpResult)
        // todo websocket/polling etc
    }.flowOn(Dispatchers.IO)

    private suspend fun getEvmTokenAccountsHttp(address: String, chainId: EvmChain?): Result<List<EvmTokenAccount>> =
        withContext(Dispatchers.IO) {
            try {
                val baseUrl = evmHttpResolver.resolveEvmUrl(chainId)
                val url: String
                when (baseUrl.first) {
                    MORALIS_API_URL -> {
                        val moralisChain = chainId?.chain ?: "eth"
                        url = "${MORALIS_API_URL}/wallets/$address/tokens?chain=$moralisChain"
                        val response = KtorClient.httpClient.get(url) {
                            header("X-API-Key", baseUrl.second?.trim().orEmpty())
                        }.body<MoralisWalletTokensResponseDto>()
                        Result.Success(response.toEvmTokenAccounts(chainId ?: EvmChain.ETHEREUM))
                    }
                    ALCHEMY_API_URL -> {
                        url = "${baseUrl.first+baseUrl.second}/assets/tokens/by-address"
                        val networks = chainId?.let { listOf(it.toAlchemyNetwork()) } ?: EvmChain.entries.map { it.toAlchemyNetwork() }
                        val allTokens = paginateAlchemyByCursor { pageKey ->
                            val response = KtorClient.httpClient.post(url) {
                                contentType(ContentType.Application.Json)
                                setBody(
                                    AlchemyTokenAccountsRequest(
                                        addresses = listOf(
                                            AlchemyAddressRequest(
                                                address = address,
                                                networks = networks,
                                            )
                                        ),
                                        pageKey = pageKey,
                                    )
                                )
                            }.body<AlchemyTokensByAddressResponseDto>()
                            AlchemyCursorPage(
                                items = response.data?.tokens.orEmpty(),
                                nextCursor = response.data?.pageKey,
                            )
                        }
                        Result.Success(allTokens.toDomainModelFromAlchemy())
                    }
                    else -> {
                        Result.Failure("Unknown RPC provider")
                    }
                }

            } catch (e: Exception) {
                Result.Failure(e.message ?: "Unknown error fetching ERC-20 tokens")
            }

        }

}