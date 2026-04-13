package com.mrf.tghost.chain.evm.domain.model

data class EvmTokenBalances(
    val address: String,
    val tokenBalances: List<EvmTokenBalance>
)

data class EvmTokenBalance(
    val contractAddress: String,
    val tokenBalance: String? = null, // Hex string like "0x..."
    val error: String? = null
)
