package com.mrf.tghost.chain.evm.data.repository

import com.mrf.tghost.data.network.client.KtorClient
import com.mrf.tghost.domain.model.Result
import com.mrf.tghost.chain.evm.data.network.mappers.toEvmStakingPositions
import com.mrf.tghost.chain.evm.data.network.model.EvmStakingProtocolDto
import com.mrf.tghost.chain.evm.domain.model.EvmStakingProtocol
import com.mrf.tghost.chain.evm.domain.repository.EvmStakingRepository
import com.mrf.tghost.chain.evm.utils.MORALIS_API_BASE_URL
import com.mrf.tghost.domain.model.RpcProviderId
import com.mrf.tghost.domain.repository.PreferencesRepository
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class EvmStakingRepositoryImpl @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
) : EvmStakingRepository {

    // TODO Moralis dovrebbe essere gestito meglio....
    override fun evmStakingAccounts(
        publicKey: String,
        evmChainId: String?
    ): Flow<Result<List<EvmStakingProtocol>>> = flow {
        try {
            val apiKey = preferencesRepository.getRpcProviderApiKey(RpcProviderId.MORALIS).first()?.trim().orEmpty()
            if (apiKey.isEmpty()) {
                emit(Result.Failure("Moralis API key missing. Add it in Network settings for the Moralis provider."))
                return@flow
            }
            val url = "${MORALIS_API_BASE_URL}/wallets/$publicKey/defi/positions?chain=$evmChainId"
            val response = KtorClient.httpClient.get(url) {
                header("X-API-Key", apiKey)
            }.body<List<EvmStakingProtocolDto>>()
            emit(Result.Success(response.toEvmStakingPositions()))
        } catch (e: Exception) {
            emit(Result.Failure(e.message ?: "Unknown error fetching Moralis tokens"))
        }
    }.flowOn(Dispatchers.IO)

}