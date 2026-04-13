package com.mrf.tghost.chain.tezos.domain.repository

import com.mrf.tghost.chain.tezos.domain.model.TezosAccountDelegation
import com.mrf.tghost.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface TezosStakingRepository {
    fun tezosStakingAccounts(publicKey: String): Flow<Result<List<TezosAccountDelegation>>?>
}