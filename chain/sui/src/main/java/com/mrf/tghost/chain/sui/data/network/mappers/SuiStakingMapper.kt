package com.mrf.tghost.chain.sui.data.network.mappers

import com.mrf.tghost.chain.sui.data.network.model.SuiStakesDto
import com.mrf.tghost.chain.sui.domain.model.SuiStake
import java.math.BigDecimal

fun SuiStakesDto.toSuiStakes(): List<SuiStake> {
    return stakes.map { stakeObject ->
        val principalBd = stakeObject.principal.toBigDecimal()
        val rewards = stakeObject.estimatedReward?.toBigDecimalOrNull() ?: BigDecimal.ZERO
        SuiStake(
            address = stakeObject.stakedSuiId,
            poolId = stakingPool,
            principal = principalBd,
            estimatedReward = rewards,
            stakeStatus = stakeObject.status
        )
    }
}


