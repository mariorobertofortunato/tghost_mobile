package com.mrf.tghost.domain.usecase

import com.mrf.tghost.domain.model.Result
import com.mrf.tghost.domain.model.metadata.TokenOffChainMetadata
import com.mrf.tghost.domain.repository.OffChainMetadataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetOffChainMetadataUseCase @Inject constructor(
    private val offChainMetadataRepository: OffChainMetadataRepository
) {
    fun getOffChainMetadata(url: String): Flow<Result<TokenOffChainMetadata?>> {
        return offChainMetadataRepository.getOffChainMetadata(url)
    }
}
