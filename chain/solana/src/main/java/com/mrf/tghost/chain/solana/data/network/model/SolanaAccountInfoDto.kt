package com.mrf.tghost.chain.solana.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SolanaAccountInfoDto(
    @SerialName("data") val data: List<String>,
    @SerialName("lamports") val lamports: Long,
    @SerialName("owner") val owner: String,
    @SerialName("executable") val executable: Boolean,
    @SerialName("rentEpoch") val rentEpoch: ULong
)