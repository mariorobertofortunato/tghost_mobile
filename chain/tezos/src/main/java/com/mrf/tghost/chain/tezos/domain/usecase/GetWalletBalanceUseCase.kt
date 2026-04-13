package com.mrf.tghost.chain.tezos.domain.usecase

import com.mrf.tghost.chain.tezos.domain.repository.TezosWalletBalanceRepository
import com.mrf.tghost.domain.model.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class GetWalletBalanceUseCase @Inject constructor(
    private val walletBalanceRepository: TezosWalletBalanceRepository
) {
    fun balanceTezos(publicKey: String): Flow<Result<Long>?> =
        walletBalanceRepository.balanceTezos(publicKey).onStart { emit(null) }

}