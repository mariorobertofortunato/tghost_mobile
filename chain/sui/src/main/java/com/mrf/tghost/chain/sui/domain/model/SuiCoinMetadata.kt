package com.mrf.tghost.chain.sui.domain.model

data class SuiCoinMetadata(
    val decimals: Int,
    val name: String,
    val symbol: String,
    val description: String,
    val iconUrl: String? = null,
    val id: String? = null
)