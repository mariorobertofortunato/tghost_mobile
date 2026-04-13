package com.mrf.tghost.chain.solana.domain.repository

import com.mrf.tghost.chain.solana.domain.model.SolanaSplTokenAccount
import com.mrf.tghost.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface SolanaTokenAccountsRepository {
    fun solanaTokenAccounts(publicKey: String): Flow<Result<List<SolanaSplTokenAccount>>?>
}