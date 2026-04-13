package com.mrf.tghost.chain.evm.domain.repository

import com.mrf.tghost.domain.model.EvmChain
import com.mrf.tghost.domain.model.Result
import com.mrf.tghost.chain.evm.domain.model.EvmTokenAccount
import kotlinx.coroutines.flow.Flow

interface EvmTokenAccountsRepository {
    fun evmTokenAccounts(publicKey: String, chainId: EvmChain): Flow<Result<List<EvmTokenAccount>>?>
}