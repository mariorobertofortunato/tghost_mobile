package com.mrf.tghost.chain.solana.domain.model

data class SolanaSplTokenAccount(
    val pubkey: String,
    val account: AccountInfo
)

data class AccountInfo(
    val lamports: Long,
    val data: ParsedAccountData,
    val owner: String,
    val executable: Boolean,
    val rentEpoch: ULong,
    val space: Long
)

data class ParsedAccountData(
    val program: String,
    val parsed: ParsedSplTokenData,
    val space: Long
)

data class ParsedSplTokenData(
    val info: SplTokenInfo,
    val type: String
)

data class SplTokenInfo(
    val isNative: Boolean? = false,
    val mint: String,
    val owner: String,
    val state: String? = "initialized",
    val tokenAmount: TokenAmountData
)

data class TokenAmountData(
    val amount: String,
    val decimals: Int,
    val uiAmount: Double?,
    val uiAmountString: String?
)
