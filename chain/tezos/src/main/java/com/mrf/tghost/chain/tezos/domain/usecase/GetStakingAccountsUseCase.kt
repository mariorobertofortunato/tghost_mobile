package com.mrf.tghost.chain.tezos.domain.usecase

import com.mrf.tghost.chain.tezos.domain.model.TezosAccountDelegation
import com.mrf.tghost.chain.tezos.domain.repository.TezosStakingRepository
import com.mrf.tghost.domain.model.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetStakingAccountsUseCase @Inject constructor(
    private val stakingRepository: TezosStakingRepository
) {

    fun tezosStakingAccounts(publicKey: String): Flow<Result<List<TezosAccountDelegation>>?> =
        stakingRepository.tezosStakingAccounts(publicKey)

}