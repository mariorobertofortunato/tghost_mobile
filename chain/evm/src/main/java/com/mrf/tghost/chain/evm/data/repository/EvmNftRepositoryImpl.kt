package com.mrf.tghost.chain.evm.data.repository

import com.mrf.tghost.chain.evm.data.network.mappers.toDomainModel
import com.mrf.tghost.chain.evm.data.network.model.moralis.EvmNftDto
import com.mrf.tghost.chain.evm.domain.model.EvmNftResponse
import com.mrf.tghost.chain.evm.domain.repository.EvmNftRepository
import com.mrf.tghost.chain.evm.utils.MORALIS_API_KEY
import com.mrf.tghost.chain.evm.utils.MORALIS_BASE_URL
import com.mrf.tghost.data.network.client.KtorClient
import com.mrf.tghost.domain.model.Result
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.headers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class EvmNftRepositoryImpl @Inject constructor() : EvmNftRepository {

    // TODO Moralis andrebbe gestito
    override fun evmNFTSAccounts(
        publicKey: String,
        evmChainId: String
    ): Flow<Result<EvmNftResponse>> = flow {
        try {
            val url =
                "${MORALIS_BASE_URL}/$publicKey/nft?chain=$evmChainId"
            val response = KtorClient.httpClient.get(url) {
                headers {
                    append("X-API-Key", MORALIS_API_KEY)
                }
            }.body<EvmNftDto>()
            emit(Result.Success(response.toDomainModel()))
        } catch (e: Exception) {
            emit(Result.Failure(e.message ?: "Unknown error fetching Moralis NFTS"))
        }
    }.flowOn(Dispatchers.IO)


}