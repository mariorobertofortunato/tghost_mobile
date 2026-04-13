package com.mrf.tghost.chain.evm.domain.repository

import com.mrf.tghost.domain.model.EvmChain
import com.mrf.tghost.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface EvmWalletBalanceRepository {
    fun balanceEvm(publicKey: String, evmChainId: EvmChain): Flow<Result<Long>?>
}