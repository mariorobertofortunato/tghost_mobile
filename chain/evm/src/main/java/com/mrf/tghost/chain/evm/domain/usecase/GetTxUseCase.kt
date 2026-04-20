package com.mrf.tghost.chain.evm.domain.usecase

import com.mrf.tghost.chain.evm.domain.repository.EvmTxRepository
import com.mrf.tghost.domain.model.EvmChain
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class GetTxUseCase @Inject constructor(
    private val evmTxRepository: EvmTxRepository
){
    fun txEvm(publicKey: String, chainId: EvmChain?) =
        evmTxRepository.txEvm(publicKey, chainId).onStart { emit(null) }
}