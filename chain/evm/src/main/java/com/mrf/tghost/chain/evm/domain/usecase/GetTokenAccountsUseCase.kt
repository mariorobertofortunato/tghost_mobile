package com.mrf.tghost.chain.evm.domain.usecase

import com.mrf.tghost.domain.model.EvmChain
import com.mrf.tghost.domain.model.Result
import com.mrf.tghost.chain.evm.domain.model.EvmTokenAccount
import com.mrf.tghost.chain.evm.domain.repository.EvmTokenAccountsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class GetTokenAccountsUseCase @Inject constructor(
    private val tokenAccountsRepository: EvmTokenAccountsRepository
){
    fun evmTokenAccounts(publicKey: String, chainId: EvmChain): Flow<Result<List<EvmTokenAccount>>?> =
        tokenAccountsRepository.evmTokenAccounts(publicKey, chainId).onStart { emit(null) }
}