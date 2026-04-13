package com.mrf.tghost.chain.solana.domain.usecase

import com.mrf.tghost.chain.solana.domain.repository.SolanaTxRepository
import javax.inject.Inject

class GetTxUseCase  @Inject constructor(
    private val solanaTxRepository: SolanaTxRepository
){
    fun txSolana(publicKey: String) = solanaTxRepository.txSolana(publicKey)
}

