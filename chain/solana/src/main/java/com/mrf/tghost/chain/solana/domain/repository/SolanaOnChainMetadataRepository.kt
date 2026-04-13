package com.mrf.tghost.chain.solana.domain.repository

import com.mrf.tghost.domain.model.Result
import com.mrf.tghost.domain.model.metadata.TokenOnChainMetadata
import kotlinx.coroutines.flow.Flow

interface SolanaOnChainMetadataRepository {
    fun getSolanaTokenOnChainMetadata(metadataPDAString: String): Flow<Result<TokenOnChainMetadata?>>
}