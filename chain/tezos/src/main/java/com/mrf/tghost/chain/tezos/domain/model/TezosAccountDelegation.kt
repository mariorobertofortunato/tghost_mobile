package com.mrf.tghost.chain.tezos.domain.model

import java.math.BigDecimal

data class TezosAccountDelegation(
    val bakerAlias: String,
    val bakerAddress: String,
    val amount: BigDecimal,
    val active: String
)