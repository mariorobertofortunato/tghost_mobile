package com.mrf.tghost.chain.tezos.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class TezosActivityDto(
    @SerialName("type") val type: String,
    @SerialName("id") val id: Long,
    @SerialName("level") val level: Int? = null,
    @SerialName("timestamp") val timestamp: String? = null,
    @SerialName("block") val block: String? = null,
    @SerialName("hash") val hash: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("bakerFee") val bakerFee: Long? = null,
    @SerialName("storageFee") val storageFee: Long? = null,
    @SerialName("allocationFee") val allocationFee: Long? = null,
    /** Microtez for operations; raw amount (often string) for `token_transfer` activity items. */
    @SerialName("amount") val amount: JsonElement? = null,
    @SerialName("sender") val sender: TezosAliasDto? = null,
    @SerialName("target") val target: TezosAliasDto? = null,
    @SerialName("initiator") val initiator: TezosAliasDto? = null,
    @SerialName("prevDelegate") val prevDelegate: TezosAliasDto? = null,
    @SerialName("newDelegate") val newDelegate: TezosAliasDto? = null,
    @SerialName("parameter") val parameter: JsonElement? = null,
    @SerialName("errors") val errors: List<JsonElement>? = null,
    @SerialName("from") val from: TezosAliasDto? = null,
    @SerialName("to") val to: TezosAliasDto? = null,
    @SerialName("token") val token: TezosActivityTokenInfoDto? = null,
    @SerialName("transactionId") val transactionId: Long? = null,
    @SerialName("originationId") val originationId: Long? = null,
    @SerialName("migrationId") val migrationId: Long? = null,
)

@Serializable
data class TezosAliasDto(
    @SerialName("address") val address: String,
    @SerialName("alias") val alias: String? = null,
)

@Serializable
data class TezosActivityTokenInfoDto(
    @SerialName("id") val id: Long? = null,
    @SerialName("contract") val contract: TezosAliasDto? = null,
    @SerialName("tokenId") val tokenId: String? = null,
    @SerialName("standard") val standard: String? = null,
    @SerialName("metadata") val metadata: JsonElement? = null,
)
