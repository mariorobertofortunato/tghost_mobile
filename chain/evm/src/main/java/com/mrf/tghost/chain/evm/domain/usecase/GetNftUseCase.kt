package com.mrf.tghost.chain.evm.domain.usecase

import com.mrf.tghost.domain.model.Result
import com.mrf.tghost.chain.evm.domain.model.EvmNftResponse
import com.mrf.tghost.chain.evm.domain.repository.EvmNftRepository
import com.mrf.tghost.domain.model.EvmChain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class GetNftUseCase @Inject constructor(
    private val nftRepository: EvmNftRepository
){
    fun evmNFTSAccounts(publicKey: String, evmChainId: EvmChain?): Flow<Result<EvmNftResponse>?> =
        nftRepository.evmNFTSAccounts(publicKey, evmChainId).onStart { emit(null) }
}