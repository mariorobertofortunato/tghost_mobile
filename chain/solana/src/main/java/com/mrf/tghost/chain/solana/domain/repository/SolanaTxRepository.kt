package com.mrf.tghost.chain.solana.domain.repository

import com.mrf.tghost.domain.model.Transaction
import kotlinx.coroutines.flow.Flow
import com.mrf.tghost.domain.model.Result

interface SolanaTxRepository {
    fun txSolana(publicKey: String): Flow<Result<List<Transaction>>?>
}
