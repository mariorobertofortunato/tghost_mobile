package com.mrf.tghost.chain.solana.domain.usecase

import com.mrf.tghost.chain.solana.domain.repository.SolanaWalletBalanceRepository
import com.mrf.tghost.domain.model.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class GetWalletBalanceUseCase @Inject constructor(
    private val solanaWalletBalanceRepository: SolanaWalletBalanceRepository
) {

    fun balanceSolana(publicKey: String): Flow<Result<Long>?> =
        solanaWalletBalanceRepository.balanceSolana(publicKey).onStart { emit(null) }

}