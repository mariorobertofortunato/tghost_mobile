package com.mrf.tghost.chain.evm.domain.usecase

import com.mrf.tghost.domain.model.EvmChain
import com.mrf.tghost.domain.model.Result
import com.mrf.tghost.chain.evm.domain.model.EvmMetadataResponse
import com.mrf.tghost.chain.evm.domain.repository.EvmOnChainMetadataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetOnChainMetadataUseCase @Inject constructor(
    private val onChainMetadataRepository: EvmOnChainMetadataRepository
){
    fun getEvmTokenOnChainMetadata(address: String, evmChain: EvmChain): Flow<Result<EvmMetadataResponse?>> =
        onChainMetadataRepository.getEvmTokenOnChainMetadata(address, evmChain)
}