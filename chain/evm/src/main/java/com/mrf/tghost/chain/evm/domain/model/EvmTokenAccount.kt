package com.mrf.tghost.chain.evm.domain.model

import java.math.BigDecimal

data class EvmTokenAccount(
    val contractAddress: String,
    val name: String?,
    val symbol: String?,
    val decimals: Int?,
    val balance: BigDecimal,
    val rawBalance: String,
    val ownerAddress: String? = null,
    val network: String? = null,
    val logo: String? = null,
    val prices: List<EvmTokenPrice> = emptyList(),
    /** True when balance is chain native currency (Alchemy `tokenAddress` null). */
    val isNative: Boolean = false,
)

data class EvmTokenPrice(
    val currency: String? = null,
    val value: String? = null,
    val lastUpdatedAt: String? = null,
)