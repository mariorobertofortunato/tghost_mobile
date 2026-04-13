package com.mrf.tghost.chain.tezos.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TezosDelegationDto(
    @SerialName("type") val type: String,
    @SerialName("id") val id: Long,
    @SerialName("level") val level: Int,
    @SerialName("timestamp") val timestamp: String,
    @SerialName("block") val block: String,
    @SerialName("hash") val hash: String,
    @SerialName("delegator") val delegator: TezosDelegatorDto,
    @SerialName("prevDelegate") val prevDelegate: TezosDelegateDto?,
    @SerialName("newDelegate") val newDelegate: TezosDelegateDto?,
    @SerialName("amount") val amount: Long,
    @SerialName("status") val status: String,
)

@Serializable
data class TezosDelegatorDto(
    @SerialName("address") val address: String,
    @SerialName("alias") val alias: String? = null
)

@Serializable
data class TezosDelegateDto(
    @SerialName("address") val address: String,
    @SerialName("alias") val alias: String? = null
)
