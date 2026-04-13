package com.mrf.tghost.chain.solana.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class SolanaTransactionDto(
    @SerialName("blockTime") val blockTime: Long? = null,
    @SerialName("meta") val meta: SolanaTransactionMetaDto? = null,
    @SerialName("slot") val slot: Long? = null,
    @SerialName("transaction") val transaction: SolanaTransactionDetailsDto? = null,
    @SerialName("version") val version: JsonElement? = null
)

@Serializable
data class SolanaTransactionMetaDto(
    @SerialName("err") val err: JsonElement? = null,
    @SerialName("fee") val fee: Long? = null,
    @SerialName("innerInstructions") val innerInstructions: List<JsonElement>? = null,
    @SerialName("preBalances") val preBalances: List<Long>? = null,
    @SerialName("postBalances") val postBalances: List<Long>? = null,
    @SerialName("preTokenBalances") val preTokenBalances: List<SolanaTokenBalanceDto>? = null,
    @SerialName("postTokenBalances") val postTokenBalances: List<SolanaTokenBalanceDto>? = null,
    @SerialName("logMessages") val logMessages: List<String>? = null,
    @SerialName("status") val status: JsonElement? = null,
    @SerialName("computeUnitsConsumed") val computeUnitsConsumed: Long? = null,
    @SerialName("costUnits") val costUnits: Long? = null,
    @SerialName("loadedAddresses") val loadedAddresses: SolanaLoadedAddressesDto? = null,
    @SerialName("rewards") val rewards: List<JsonElement>? = null
)

@Serializable
data class SolanaTokenBalanceDto(
    @SerialName("accountIndex") val accountIndex: Int,
    @SerialName("mint") val mint: String,
    @SerialName("owner") val owner: String? = null,
    @SerialName("uiTokenAmount") val uiTokenAmount: SolanaUiTokenAmountDto
)

@Serializable
data class SolanaUiTokenAmountDto(
    @SerialName("amount") val amount: String,
    @SerialName("decimals") val decimals: Int,
    @SerialName("uiAmount") val uiAmount: Double? = null,
    @SerialName("uiAmountString") val uiAmountString: String? = null
)

@Serializable
data class SolanaLoadedAddressesDto(
    @SerialName("readonly") val readonly: List<String>? = null,
    @SerialName("writable") val writable: List<String>? = null
)

@Serializable
data class SolanaTransactionDetailsDto(
    @SerialName("message") val message: SolanaTransactionMessageDto? = null,
    @SerialName("signatures") val signatures: List<String>? = null
)

@Serializable
data class SolanaTransactionMessageDto(
    @SerialName("accountKeys") val accountKeys: List<String>? = null,
    @SerialName("header") val header: SolanaTransactionHeaderDto? = null,
    @SerialName("instructions") val instructions: List<SolanaTransactionInstructionDto>? = null,
    @SerialName("recentBlockhash") val recentBlockhash: String? = null
)

@Serializable
data class SolanaTransactionHeaderDto(
    @SerialName("numReadonlySignedAccounts") val numReadonlySignedAccounts: Int? = null,
    @SerialName("numReadonlyUnsignedAccounts") val numReadonlyUnsignedAccounts: Int? = null,
    @SerialName("numRequiredSignatures") val numRequiredSignatures: Int? = null
)

@Serializable
data class SolanaTransactionInstructionDto(
    @SerialName("accounts") val accounts: List<Int>? = null,
    @SerialName("data") val data: String? = null,
    @SerialName("programIdIndex") val programIdIndex: Int? = null,
    @SerialName("stackHeight") val stackHeight: Int? = null
)
