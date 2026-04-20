package com.mrf.tghost.chain.evm.domain.usecase

import com.mrf.tghost.domain.model.Result
import com.mrf.tghost.chain.evm.domain.model.EvmStakingProtocol
import com.mrf.tghost.chain.evm.domain.repository.EvmStakingRepository
import com.mrf.tghost.domain.model.EvmChain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class GetStakingAccountsUseCase @Inject constructor(
    private val stakingRepository: EvmStakingRepository
) {
    fun evmStakingAccounts(publicKey: String, evmChainId: EvmChain?): Flow<Result<List<EvmStakingProtocol>>?> =
        stakingRepository.evmStakingAccounts(publicKey, evmChainId).onStart { emit(null) }
}