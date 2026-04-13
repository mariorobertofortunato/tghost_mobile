package com.mrf.tghost.chain.sui.domain.repository

import com.mrf.tghost.chain.sui.domain.model.SuiStake
import com.mrf.tghost.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface SuiStakingRepository {
    fun suiStakingAccounts(publicKey: String): Flow<Result<List<SuiStake>>?>
}
