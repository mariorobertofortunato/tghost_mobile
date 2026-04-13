package com.mrf.tghost.data.repository

import com.mrf.tghost.data.network.client.KtorClient
import com.mrf.tghost.data.network.http.factory.HttpRequestFactory
import com.mrf.tghost.data.network.mappers.toDomainModel
import com.mrf.tghost.data.network.model.MarketDataInfoDto
import com.mrf.tghost.data.utils.TZKT_API_URL
import com.mrf.tghost.domain.model.TokenMarketDataInfo
import com.mrf.tghost.domain.model.Result
import com.mrf.tghost.domain.model.SupportedChain
import com.mrf.tghost.domain.repository.MarketDataRepository
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.double
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject

// TODO refactorare meglio, anche in virtu del fatto che per tezos l'api ritorna semplicemente il prezzo,
//  mentre per le altre chain abbiamo un'api piu ricca

class MarketDataRepositoryImpl @Inject constructor() : MarketDataRepository {
    override fun fetchMarketData(
        marketDataUrl: String?,
        address: String?,
        chain: SupportedChain,
        customDexChainId: String?
    ): Flow<Result<TokenMarketDataInfo?>> = flow {
        emit(Result.Loading)

        if (chain == SupportedChain.TEZ) {
            try {
                val url = "${TZKT_API_URL}/quotes/last"
                val response = KtorClient.httpClient.get(url).bodyAsText()
                val jsonBody = Json.Default
                    .parseToJsonElement(response)
                    .jsonObject

                val price = jsonBody["usd"]?.jsonPrimitive?.double ?: 0.0
                //emit (Result.Success(price))
                emit(
                    Result.Success(
                        TokenMarketDataInfo(
                            chainId = SupportedChain.TEZ.chain.name,
                            priceUsd = price.toString()
                        )
                    )
                )
            } catch (e: Exception) {
                emit(Result.Failure(e.message ?: "Error fetching Tezos price"))
            }
        } else {
            val dexChainId = if (chain == SupportedChain.EVM && customDexChainId != null) {
                customDexChainId
            } else {
                when (chain) {
                    SupportedChain.SOLANA -> "solana"
                    SupportedChain.EVM -> "ethereum"
                    SupportedChain.SUI -> "sui"
                    SupportedChain.TEZ -> "tezos"
                }
            }

            val tokenUrl = "$marketDataUrl/$dexChainId/$address"
            val response = HttpRequestFactory.makeHttpRequest(tokenUrl)
            if (response is Result.Success) {
                val marketDataInfoDtos: List<MarketDataInfoDto>? = response.data?.body()
                val marketDataInfoDto = marketDataInfoDtos?.firstOrNull()
                emit(Result.Success(marketDataInfoDto?.toDomainModel()))
            }
            if (response is Result.Failure) {
                emit(Result.Failure(response.errorMessage))
            }
        }


    }
}
