package com.mrf.tghost.chain.solana.domain.repository

import com.mrf.tghost.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface SolanaWalletBalanceRepository {
    fun balanceSolana(publicKey: String): Flow<Result<Long>?>
}