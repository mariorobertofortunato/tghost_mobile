package com.mrf.tghost.chain.solana.domain.repository

import com.mrf.tghost.chain.solana.domain.model.SolanaStake
import com.mrf.tghost.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface SolanaStakingRepository {

    fun solanaStakingAccounts(publicKey: String): Flow<Result<List<SolanaStake>>?>

}