package com.mrf.tghost.domain.model

data class Wallet (
    val publicKey: String,
    val name: String,
    val chainId: SupportedChainId,
    val snapshot: WalletSnapshot?,
)

data class WalletSnapshot(
    val timestamp: Long = System.currentTimeMillis(),
    val balanceUSd: Double = 0.0,
    val balanceNative: Double = 0.0
)