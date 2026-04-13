package com.mrf.tghost.chain.tezos.domain.repository

import com.mrf.tghost.domain.model.Result
import com.mrf.tghost.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TezosTxRepository {
    fun txTezos(publicKey: String): Flow<Result<List<Transaction>>?>
}