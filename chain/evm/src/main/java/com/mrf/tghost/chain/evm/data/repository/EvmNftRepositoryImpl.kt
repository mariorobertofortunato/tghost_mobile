package com.mrf.tghost.chain.evm.data.repository

import com.mrf.tghost.chain.evm.data.network.mappers.toDomainModel
import com.mrf.tghost.chain.evm.data.network.mappers.toEvmNftResponse
import com.mrf.tghost.chain.evm.data.network.model.alchemy.AlchemyNftByAddressAddressEntryDto
import com.mrf.tghost.chain.evm.data.network.model.alchemy.AlchemyNftsByAddressRequestDto
import com.mrf.tghost.chain.evm.data.network.model.alchemy.AlchemyNftsByAddressResponseDto
import com.mrf.tghost.chain.evm.data.network.model.moralis.MoralisEvmNftDto
import com.mrf.tghost.chain.evm.data.network.resolver.http.EvmHttpResolver
import com.mrf.tghost.chain.evm.domain.model.EvmNftResponse
import com.mrf.tghost.chain.evm.domain.repository.EvmNftRepository
import com.mrf.tghost.chain.evm.utils.ALCHEMY_API_BASE_URL
import com.mrf.tghost.chain.evm.utils.MORALIS_API_BASE_URL
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
import javax.inject.Inject

class EvmNftRepositoryImpl @Inject constructor(
    private val evmHttpResolver: EvmHttpResolver,
) : EvmNftRepository {

    override fun evmNFTSAccounts(
        publicKey: String,
        evmChainId: EvmChain?,
    ): Flow<Result<EvmNftResponse>?> = flow {
        emit(getEvmNFTSAccountsHttp(publicKey, evmChainId))
        // todo websocket/polling etc
    }.flowOn(Dispatchers.IO)

    private suspend fun getEvmNFTSAccountsHttp(
        address: String,
        chainId: EvmChain?,
    ): Result<EvmNftResponse> =
        withContext(Dispatchers.IO) {
            try {
                val baseUrl = evmHttpResolver.resolveEvmUrl(chainId)
                when (baseUrl.first) {
                    MORALIS_API_BASE_URL -> {
                        val apiKey = baseUrl.second?.trim().orEmpty()
                        if (apiKey.isEmpty()) {
                            return@withContext Result.Failure("Moralis API key missing.")
                        }
                        val moralisChain = chainId?.chain ?: "eth"
                        val url =
                            "${MORALIS_API_BASE_URL}/$address/nft?chain=$moralisChain&format=decimal&normalizeMetadata=true"
                        val response = KtorClient.httpClient.get(url) {
                            header("X-API-Key", apiKey)
                        }.body<MoralisEvmNftDto>()
                        Result.Success(response.toDomainModel())
                    }

                    ALCHEMY_API_BASE_URL -> {
                        val apiKey = baseUrl.second?.trim().orEmpty()
                        if (apiKey.isEmpty()) {
                            return@withContext Result.Failure("Alchemy API key missing.")
                        }
                        val url = "${baseUrl.first}$apiKey/assets/nfts/by-address"
                        val networks = chainId?.let { listOf(it.toAlchemyNetwork()) }
                            ?: EvmChain.entries.map { it.toAlchemyNetwork() }
                        val body = AlchemyNftsByAddressRequestDto(
                            addresses = listOf(
                                AlchemyNftByAddressAddressEntryDto(
                                    address = address,
                                    networks = networks,
                                    excludeFilters = listOf("SPAM"),
                                    spamConfidenceLevel = "VERY_HIGH",
                                ),
                            ),
                        )
                        val response = KtorClient.httpClient.post(url) {
                            contentType(ContentType.Application.Json)
                            setBody(body)
                        }.body<AlchemyNftsByAddressResponseDto>()
                        Result.Success(response.toEvmNftResponse())
                    }

                    else -> Result.Failure("Unknown RPC provider")
                }
            } catch (e: Exception) {
                Result.Failure(e.message ?: "Unknown error fetching NFTs")
            }
        }
}
