package com.mrf.tghost.chain.evm.domain.usecase

import com.mrf.tghost.domain.model.EvmChain
import com.mrf.tghost.domain.model.Result
import com.mrf.tghost.chain.evm.domain.repository.EvmWalletBalanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class GetWalletBalanceUseCase @Inject constructor(
    private val walletBalanceRepository: EvmWalletBalanceRepository
){

    fun balanceEvm(publicKey: String, evmChainId: EvmChain): Flow<Result<Long>?> =
        walletBalanceRepository.balanceEvm(publicKey, evmChainId).onStart { emit(null) }

}