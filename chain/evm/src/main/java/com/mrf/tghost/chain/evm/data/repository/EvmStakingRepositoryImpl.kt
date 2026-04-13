package com.mrf.tghost.chain.evm.data.repository

import com.mrf.tghost.data.network.client.KtorClient
import com.mrf.tghost.domain.model.Result
import com.mrf.tghost.chain.evm.data.network.mappers.toEvmStakingPositions
import com.mrf.tghost.chain.evm.data.network.model.EvmStakingProtocolDto
import com.mrf.tghost.chain.evm.domain.model.EvmStakingProtocol
import com.mrf.tghost.chain.evm.domain.repository.EvmStakingRepository
import com.mrf.tghost.chain.evm.utils.MORALIS_API_KEY
import com.mrf.tghost.chain.evm.utils.MORALIS_BASE_URL
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.headers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class EvmStakingRepositoryImpl @Inject constructor() : EvmStakingRepository {

    // TODO Moralis dovrebbe essere gestito meglio....
    override fun evmStakingAccounts(
        publicKey: String,
        evmChainId: String
    ): Flow<Result<List<EvmStakingProtocol>>> = flow {
        try {
            val url = "${MORALIS_BASE_URL}/wallets/$publicKey/defi/positions?chain=$evmChainId"
            val response = KtorClient.httpClient.get(url) {
                headers { append("X-API-Key", MORALIS_API_KEY) }
            }.body<List<EvmStakingProtocolDto>>()
            emit(Result.Success(response.toEvmStakingPositions()))
        } catch (e: Exception) {
            emit(Result.Failure(e.message ?: "Unknown error fetching Moralis tokens"))
        }
    }.flowOn(Dispatchers.IO)

}