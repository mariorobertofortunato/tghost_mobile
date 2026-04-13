package com.mrf.tghost.chain.tezos.domain.repository

import com.mrf.tghost.chain.tezos.domain.model.TezosToken
import com.mrf.tghost.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface TezosTokenAccountsRepository {
    fun tezosTokenAccounts(publicKey: String): Flow<Result<List<TezosToken>>?>
}