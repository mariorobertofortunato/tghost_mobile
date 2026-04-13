package com.mrf.tghost.chain.solana.domain.usecase

import com.mrf.tghost.chain.solana.domain.repository.SolanaOnChainMetadataRepository
import com.mrf.tghost.domain.model.Result
import com.mrf.tghost.domain.model.metadata.TokenOnChainMetadata
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetOnChainMetadataUseCase @Inject constructor(
    private val solanaOnChainMetadataRepository: SolanaOnChainMetadataRepository
) {

    fun getSolanaTokenOnChainMetadata(metadataPDAString: String): Flow<Result<TokenOnChainMetadata?>> =
        solanaOnChainMetadataRepository.getSolanaTokenOnChainMetadata(metadataPDAString)

}
