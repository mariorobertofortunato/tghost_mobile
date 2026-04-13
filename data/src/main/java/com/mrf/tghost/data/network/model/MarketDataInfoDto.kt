package com.mrf.tghost.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MarketDataInfoDto(
    @SerialName("chainId") val chainId: String? = null,
    @SerialName("dexId") val dexId: String? = null,
    @SerialName("url") val url: String? = null,
    @SerialName("pairAddress") val pairAddress: String? = null,
    @SerialName("labels") val labels: List<String> = emptyList(),
    @SerialName("baseToken") val baseToken: TokenDto? = null,
    @SerialName("quoteToken") val quoteToken: TokenDto? = null,
    @SerialName("priceNative") val priceNative: String? = null,
    @SerialName("priceUsd") val priceUsd: String? = null,
    @SerialName("txns") val txns: TransactionsDto? = null,
    @SerialName("volume") val volume: VolumeDto? = null,
    @SerialName("priceChange") val priceChange: PriceChangeDto? = null,
    @SerialName("liquidity") val liquidity: LiquidityDto? = null,
    @SerialName("fdv") val fdv: Double? = null,
    @SerialName("marketCap") val marketCap: Double? = null,
    @SerialName("pairCreatedAt") val pairCreatedAt: Long? = null,
    @SerialName("info") val info: MarketDataTokenInfoDto? = null
) {
    val priceUsdDouble: Double? get() = priceUsd?.toDouble()
    val priceNativeDouble: Double? get() = priceNative?.toDouble()
}

@Serializable
data class TokenDto(
    @SerialName("address") val address: String,
    @SerialName("name") val name: String,
    @SerialName("symbol") val symbol: String
)

@Serializable
data class TransactionsDto(
    @SerialName("m5") val m5: TransactionPairDto? = null,
    @SerialName("h1") val h1: TransactionPairDto? = null,
    @SerialName("h6") val h6: TransactionPairDto? = null,
    @SerialName("h24") val h24: TransactionPairDto? = null
)

@Serializable
data class TransactionPairDto(
    @SerialName("buys") val buys: Int? = null,
    @SerialName("sells") val sells: Int? = null
)

@Serializable
data class VolumeDto(
    @SerialName("h24") val h24: Double? = null,
    @SerialName("h6") val h6: Double? = null,
    @SerialName("h1") val h1: Double? = null,
    @SerialName("m5") val m5: Double? = null
)

@Serializable
data class PriceChangeDto(
    @SerialName("m5") val m5: Double? = null,
    @SerialName("h1") val h1: Double? = null,
    @SerialName("h6") val h6: Double? = null,
    @SerialName("h24") val h24: Double? = null
)

@Serializable
data class LiquidityDto(
    @SerialName("usd") val usd: Double? = null,
    @SerialName("base") val base: Double? = null,
    @SerialName("quote") val quote: Double? = null
)

@Serializable
data class SocialDto(
    @SerialName("type") val type: String,
    @SerialName("url") val url: String
)

@Serializable
data class MarketDataTokenInfoDto(
    @SerialName("imageUrl") val imageUrl: String? = null,
    @SerialName("header") val header: String? = null,
    @SerialName("openGraph") val openGraph: String? = null,
    @SerialName("websites") val websites: List<WebsiteDto> = emptyList(),
    @SerialName("socials") val socials: List<SocialDto> = emptyList()
)

@Serializable
data class WebsiteDto(
    @SerialName("label") val label: String? = null,
    @SerialName("url") val url: String? = null
)
