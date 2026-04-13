package com.mrf.tghost.domain.model

/**
 * Common representation of a wallet processing update, independent of chain.
 * Used by all chain processors when managing multiple wallets across chains.
 */
sealed class WalletUpdate {
    data class LoadingStage(val stage: String?) : WalletUpdate()
    data class Success(
        val totalUsd: Double = 0.0,
        val totalNative: Double = 0.0,
        val tokens: List<TokenAccount> = emptyList(),
        val transactions: List<Transaction> = emptyList()
    ) : WalletUpdate()

    data class Error(val message: String) : WalletUpdate()
}
