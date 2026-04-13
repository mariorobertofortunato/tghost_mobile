package com.mrf.tghost.chain.sui.domain.repository

import com.mrf.tghost.domain.model.Result
import com.mrf.tghost.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface SuiWalletActivityRepository {
    fun suiWalletActivity(walletAddress: String): Flow<Result<List<Transaction>>?>
}
