package com.mrf.tghost.domain.model

data class TokenMarketDataInfo(
    val chainId: String? = null,
    val dexId: String? = null,
    val url: String? = null,
    val pairAddress: String? = null,
    val labels: List<String> = emptyList(),
    val baseToken: Token? = null,
    val quoteToken: Token? = null,
    val priceNative: String? = null,
    val priceUsd: String? = null,
    val txns: Transactions? = null,
    val volume: Volume? = null,
    val priceChange: PriceChange? = null,
    val liquidity: Liquidity? = null,
    val fdv: Double? = null,
    val marketCap: Double? = null,
    val pairCreatedAt: Long? = null,
    val info: TokenMarketData? = null
) {
    val priceUsdDouble: Double? get() = priceUsd?.toDouble()
    val priceNativeDouble: Double? get() = priceNative?.toDouble()
}

data class Token(
    val address: String,
    val name: String,
    val symbol: String
)

data class Transactions(
    val m5: TransactionPair? = null,
    val h1: TransactionPair? = null,
    val h6: TransactionPair? = null,
    val h24: TransactionPair? = null,
)

data class TransactionPair(
    val buys: Int? = null,
    val sells: Int? = null,
)

data class Volume(
    val h24: Double? = null,
    val h6: Double? = null,
    val h1: Double? = null,
    val m5: Double? = null,
)

data class PriceChange(
    val m5: Double? = null,
    val h1: Double? = null,
    val h6: Double? = null,
    val h24: Double? = null,
)

data class Liquidity(
    val usd: Double? = null,
    val base: Double? = null,
    val quote: Double? = null,
)

data class Social(
    val type: String,
    val url: String
)

data class TokenMarketData(
    val imageUrl: String? = null,
    val header: String? = null,
    val openGraph: String? = null,
    val websites: List<Website> = emptyList(),
    val socials: List<Social> = emptyList()
)

data class Website(
    val label: String? = null,
    val url: String? = null
)
