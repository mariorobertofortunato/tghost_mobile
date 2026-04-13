package com.mrf.tghost.chain.sui.domain.repository

import com.mrf.tghost.chain.sui.domain.model.SuiCoinMetadata
import com.mrf.tghost.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface SuiOnChainMetadataRepository {
    fun getSuiCoinMetadata(coinType: String): Flow<Result<SuiCoinMetadata?>>
}
