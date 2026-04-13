package com.mrf.tghost.chain.evm.domain.repository

import com.mrf.tghost.domain.model.Result
import com.mrf.tghost.chain.evm.domain.model.EvmStakingProtocol
import kotlinx.coroutines.flow.Flow

interface EvmStakingRepository {
    fun evmStakingAccounts(publicKey: String, evmChainId: String): Flow<Result<List<EvmStakingProtocol>>?>
}