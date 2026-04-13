package com.mrf.tghost.domain.model

data class Transaction(
    val id: String,
    val chain: String,
    val blockNumber: Long?,
    val timestamp: Long?,
    val fee: Long?,
    val isSuccess: Boolean,
    val error: String?,
    val balanceChanges: List<BalanceChange>,
    val type: TransactionType = TransactionType.UNKNOWN
)

data class BalanceChange(
    val address: String,
    val amount: Long, // in base units (lamports, wei, etc.)
    val symbol: String,
    val decimals: Int,
    val isNative: Boolean,
    val mint: String? = null // Contract address or Mint
)

enum class TransactionType {
    TRANSFER,
    SWAP,
    STAKE,
    UNSTAKE,
    CONTRACT_INTERACTION,
    UNKNOWN
}
