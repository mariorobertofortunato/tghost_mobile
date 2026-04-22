package com.mrf.tghost.chain.solana.domain.model

import java.math.BigDecimal

data class SolanaStake(
    val pubkey: String,
    val validatorAddress: String,
    val amount: BigDecimal,
    val status: String,
    val activationEpoch: ULong
)