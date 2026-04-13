package com.mrf.tghost.chain.tezos.domain.usecase

import com.mrf.tghost.chain.tezos.domain.repository.TezosTxRepository
import javax.inject.Inject

class GetTxUseCase  @Inject constructor(
    private val tezosTxRepository: TezosTxRepository
){
    fun txTezos(publicKey: String) = tezosTxRepository.txTezos(publicKey)
}