package com.mrf.tghost.domain.usecase

import com.mrf.tghost.domain.model.TokenMarketDataInfo
import com.mrf.tghost.domain.model.Result
import com.mrf.tghost.domain.model.SupportedChain
import com.mrf.tghost.domain.repository.MarketDataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMarketDataUseCase @Inject constructor(
    private val marketDataRepository: MarketDataRepository
) {
    fun fetchMarketDataInfo(
        marketDataUrl: String?,
        address: String?,
        chain: SupportedChain,
        customDexChainId: String? = null
    ): Flow<Result<TokenMarketDataInfo?>> {
        return marketDataRepository.fetchMarketData(
            marketDataUrl = marketDataUrl,
            address = address,
            chain = chain,
            customDexChainId = customDexChainId
        )
    }
}
