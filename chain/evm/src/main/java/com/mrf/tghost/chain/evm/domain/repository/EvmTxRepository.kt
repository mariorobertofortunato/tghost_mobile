package com.mrf.tghost.chain.evm.domain.repository

import com.mrf.tghost.domain.model.EvmChain
import com.mrf.tghost.domain.model.Result
import com.mrf.tghost.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface EvmTxRepository {
    fun txEvm(publicKey: String, chainId: EvmChain?): Flow<Result<List<Transaction>>?>
}