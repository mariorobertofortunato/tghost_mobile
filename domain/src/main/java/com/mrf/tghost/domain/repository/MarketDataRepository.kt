package com.mrf.tghost.domain.repository

import com.mrf.tghost.domain.model.TokenMarketDataInfo
import com.mrf.tghost.domain.model.Result
import com.mrf.tghost.domain.model.SupportedChain
import kotlinx.coroutines.flow.Flow

interface MarketDataRepository {
    fun fetchMarketData(
        marketDataUrl: String?,
        address: String?,
        chain: SupportedChain,
        customDexChainId: String? = null
    ): Flow<Result<TokenMarketDataInfo?>>
}
