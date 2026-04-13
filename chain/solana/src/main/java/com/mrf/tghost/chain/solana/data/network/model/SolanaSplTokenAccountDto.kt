package com.mrf.tghost.chain.solana.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SolanaSplTokenAccountDto(
    @SerialName("pubkey") val pubkey: String,
    @SerialName("account") val account: AccountInfoDto
)

@Serializable
data class AccountInfoDto(
    @SerialName("lamports") val lamports: Long,
    @SerialName("data") val data: ParsedAccountDataDto,
    @SerialName("owner") val owner: String,
    @SerialName("executable") val executable: Boolean,
    @SerialName("rentEpoch") val rentEpoch: ULong,
    @SerialName("space") val space: Long
)

@Serializable
data class ParsedAccountDataDto(
    @SerialName("program") val program: String,
    @SerialName("parsed") val parsed: ParsedSplTokenDataDto,
    @SerialName("space") val space: Long
)

@Serializable
data class ParsedSplTokenDataDto(
    @SerialName("info") val info: SplTokenInfoDto,
    @SerialName("type") val type: String
)

@Serializable
data class SplTokenInfoDto(
    @SerialName("isNative") val isNative: Boolean? = false,
    @SerialName("mint") val mint: String,
    @SerialName("owner") val owner: String,
    @SerialName("state") val state: String? = "initialized", // "initialized", "uninitialized", "frozen"
    @SerialName("tokenAmount") val tokenAmount: TokenAmountDataDto
)

@Serializable
data class TokenAmountDataDto(
    @SerialName("amount") val amount: String,
    @SerialName("decimals") val decimals: Int,  // the goddamn Number of decimals
    @SerialName("uiAmount") val uiAmount: Double?,
    @SerialName("uiAmountString") val uiAmountString: String?
)
