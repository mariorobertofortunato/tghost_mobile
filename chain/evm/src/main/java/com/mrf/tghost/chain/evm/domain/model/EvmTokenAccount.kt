package com.mrf.tghost.chain.evm.domain.model

import java.math.BigDecimal

data class EvmTokenAccount(
    val contractAddress: String,
    val name: String?,
    val symbol: String?,
    val decimals: Int?,
    val balance: BigDecimal,
    val rawBalance: String
)