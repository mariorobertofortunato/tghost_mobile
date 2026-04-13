package com.mrf.tghost.chain.solana.domain.usecase

import com.mrf.tghost.chain.solana.domain.model.SolanaStake
import com.mrf.tghost.chain.solana.domain.repository.SolanaStakingRepository
import com.mrf.tghost.domain.model.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class GetStakingAccountsUseCase @Inject constructor(
    private val solanaStakingRepository: SolanaStakingRepository
) {

    fun solanaStakingAccounts(publicKey: String): Flow<Result<List<SolanaStake>>?> =
        solanaStakingRepository.solanaStakingAccounts(publicKey).onStart { emit(null) }

}