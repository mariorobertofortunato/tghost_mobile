package com.mrf.tghost.chain.evm.data.repository

import com.mrf.tghost.chain.evm.data.network.mappers.toEvmStakingPositions
import com.mrf.tghost.chain.evm.data.network.model.moralis.EvmStakingProtocolDto
import com.mrf.tghost.chain.evm.data.network.resolver.http.EvmHttpResolver
import com.mrf.tghost.chain.evm.domain.model.EvmStakingProtocol
import com.mrf.tghost.chain.evm.domain.repository.EvmStakingRepository
import com.mrf.tghost.chain.evm.utils.MORALIS_API_URL
import com.mrf.tghost.data.network.client.KtorClient
import com.mrf.tghost.domain.model.EvmChain
import com.mrf.tghost.domain.model.Result
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EvmStakingRepositoryImpl @Inject constructor(
    private val evmHttpResolver: EvmHttpResolver,
) : EvmStakingRepository {

    // TODO Moralis dovrebbe essere gestito meglio....
    override fun evmStakingAccounts(
        publicKey: String,
        evmChainId: EvmChain?
    ): Flow<Result<List<EvmStakingProtocol>>> = flow {
        emit(getEvmStakingAccountsHttp(publicKey, evmChainId))
        // todo websocket/polling etc
    }.flowOn(Dispatchers.IO)

    private suspend fun getEvmStakingAccountsHttp(
        address: String,
        chainId: EvmChain?,
    ): Result<List<EvmStakingProtocol>> =
        withContext(Dispatchers.IO) {
            try {
                val baseUrl = evmHttpResolver.resolveEvmUrl(chainId)
                when (baseUrl.first) {
                    MORALIS_API_URL -> {
                        val apiKey = baseUrl.second?.trim().orEmpty()
                        if (apiKey.isEmpty()) {
                            return@withContext Result.Failure("Moralis API key missing.")
                        }
                        val moralisChain = chainId?.chain ?: "eth"
                        val url = "${MORALIS_API_URL}/wallets/$address/defi/positions?chain=$moralisChain"
                        val response = KtorClient.httpClient.get(url) {
                            header("X-API-Key", apiKey)
                        }.body<List<EvmStakingProtocolDto>>()
                        Result.Success(response.toEvmStakingPositions())
                    }

                    // Given that only Moralis provide an API for DeFi positions, all other provider return an empty list
                    else -> Result.Success(emptyList())
                }
            } catch (e: Exception) {
                Result.Failure(e.message ?: "Unknown error fetching NFTs")
            }
        }



}