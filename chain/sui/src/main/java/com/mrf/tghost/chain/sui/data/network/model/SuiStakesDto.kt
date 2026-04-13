package com.mrf.tghost.chain.sui.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SuiStakesDto(
    @SerialName("validatorAddress") val validatorAddress: String,
    @SerialName("stakingPool") val stakingPool: String,
    @SerialName("stakes") val stakes: List<SuiStakeObjectDto>
)

@Serializable
data class SuiStakeObjectDto(
    @SerialName("stakedSuiId") val stakedSuiId: String,
    @SerialName("stakeRequestEpoch") val stakeRequestEpoch: String,
    @SerialName("stakeActiveEpoch") val stakeActiveEpoch: String,
    @SerialName("principal") val principal: String,
    @SerialName("status") val status: String,
    @SerialName("estimatedReward") val estimatedReward: String? = null
)
