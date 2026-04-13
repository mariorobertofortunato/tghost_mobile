package com.mrf.tghost.chain.sui.domain.usecase

import com.mrf.tghost.chain.sui.domain.model.SuiStake
import com.mrf.tghost.chain.sui.domain.repository.SuiStakingRepository
import com.mrf.tghost.domain.model.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class GetStakingAccountsUseCase @Inject constructor(
    private val stakingRepository: SuiStakingRepository
) {
    fun suiStakingAccounts(publicKey: String): Flow<Result<List<SuiStake>>?> =
        stakingRepository.suiStakingAccounts(publicKey).onStart { emit(null) }

}
