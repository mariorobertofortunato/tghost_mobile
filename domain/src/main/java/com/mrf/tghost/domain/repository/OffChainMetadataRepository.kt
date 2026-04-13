package com.mrf.tghost.domain.repository

import com.mrf.tghost.domain.model.Result
import com.mrf.tghost.domain.model.metadata.TokenOffChainMetadata
import kotlinx.coroutines.flow.Flow

interface OffChainMetadataRepository {
    fun getOffChainMetadata(url: String): Flow<Result<TokenOffChainMetadata?>>
}
