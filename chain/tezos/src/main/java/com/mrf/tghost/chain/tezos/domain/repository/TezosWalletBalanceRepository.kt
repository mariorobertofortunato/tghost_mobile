package com.mrf.tghost.chain.tezos.domain.repository

import com.mrf.tghost.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface TezosWalletBalanceRepository {
    fun balanceTezos(publicKey: String): Flow<Result<Long>?>
}