package com.mrf.tghost.chain.solana.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class SolanaSignatureDto(
    @SerialName("signature") val signature: String,
    @SerialName("slot") val slot: Long? = null,
    @SerialName("err") val err: JsonElement? = null,
    @SerialName("memo") val memo: String? = null,
    @SerialName("blockTime") val blockTime: Long? = null,
    @SerialName("confirmationStatus") val confirmationStatus: String? = null
)
