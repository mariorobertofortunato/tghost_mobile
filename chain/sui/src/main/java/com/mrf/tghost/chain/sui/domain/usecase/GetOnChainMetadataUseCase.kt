package com.mrf.tghost.chain.sui.domain.usecase

import com.mrf.tghost.chain.sui.domain.model.SuiCoinMetadata
import com.mrf.tghost.chain.sui.domain.repository.SuiOnChainMetadataRepository
import com.mrf.tghost.domain.model.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetOnChainMetadataUseCase @Inject constructor(
    private val onChainMetadataRepository: SuiOnChainMetadataRepository
) {

    fun getSuiCoinMetadata(coinType: String): Flow<Result<SuiCoinMetadata?>> =
        onChainMetadataRepository.getSuiCoinMetadata(coinType)

}