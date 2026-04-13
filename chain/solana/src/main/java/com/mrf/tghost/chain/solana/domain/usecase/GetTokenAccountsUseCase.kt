package com.mrf.tghost.chain.solana.domain.usecase

import com.mrf.tghost.chain.solana.domain.model.SolanaSplTokenAccount
import com.mrf.tghost.chain.solana.domain.repository.SolanaTokenAccountsRepository
import com.mrf.tghost.domain.model.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class GetTokenAccountsUseCase @Inject constructor(
    private val solanaTokenAccountsRepository: SolanaTokenAccountsRepository
) {

    fun solanaTokenAccounts(publicKey: String): Flow<Result<List<SolanaSplTokenAccount>>?> =
        solanaTokenAccountsRepository.solanaTokenAccounts(publicKey).onStart { emit(null) }

}