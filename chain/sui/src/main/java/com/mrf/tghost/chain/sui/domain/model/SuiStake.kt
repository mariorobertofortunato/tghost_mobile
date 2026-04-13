package com.mrf.tghost.chain.sui.domain.model

import java.math.BigDecimal

data class SuiStake(
    val address: String,
    val poolId: String,
    val principal: BigDecimal,
    val estimatedReward: BigDecimal,
    val stakeStatus: String
) {
    val totalBalance: BigDecimal
        get() = principal + estimatedReward
}
