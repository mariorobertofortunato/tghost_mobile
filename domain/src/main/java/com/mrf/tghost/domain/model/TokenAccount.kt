package com.mrf.tghost.domain.model

data class TokenAccount(
    // Token
    val chainId: String? = null,
    val pubkey: String? = null,
    val pairAddress: String? = null,
    val name: String? = null,
    val symbol: String? = null,
    val uri: String? = null,
    val image: String? = null,
    val description: String? = null,
    val createdOn: String? = null,
    val labels: List<String> = emptyList(),
    val baseToken: Token? = null,
    val quoteToken: Token? = null,
    val priceNative: String? = "1",
    val priceUsd: String? = null,
    val txns: Transactions? = null,
    val volume: Volume? = null,
    val priceChange: PriceChange? = null,
    val liquidity: Liquidity? = null,
    val fdv: Double? = null,
    val marketCap: Double? = null,
    val pairCreatedAt: Long? = null,
    val info: TokenMarketData? = null,
    // Account
    val amount: String? = null,
    val decimals: Int? = null,
    val amountDouble: Double? = null,
    val uiAmountString: String? = null,
    val valueUsd: Double? = priceUsd?.toDouble()?.times(amountDouble ?: 1.0),
    val valueNative: Double? = priceNative?.toDouble()?.times(amountDouble ?: 1.0),
    val tokenAccountCategory: TokenAccountCategories? = null
)

enum class TokenAccountCategories {
    HOLDINGS, DEFI, NFTS
}

val ipfsGateways = listOf(
    "https://gateway.pinata.cloud/ipfs/",
    "https://ipfs.io/ipfs/",
    "https://nftstorage.link/ipfs/",
    "https://dweb.link/ipfs/"
)

fun ipfsToHttp(url: String, ipfsGateway: String = "https://gateway.pinata.cloud/ipfs/"): String {

    if (url.startsWith("ipfs://")) {
        val hash = url.removePrefix("ipfs://")
        return "$ipfsGateway$hash"
    }

    if (url.startsWith("https://ipfs.io/ipfs/")) {
        val hash = url.removePrefix("https://ipfs.io/ipfs/")
        return "$ipfsGateway$hash"
    }

    if (url.startsWith("https://cdn.helius-rpc.com/cdn-cgi/image//https://ipfs.io/ipfs/")) {
        val hash =
            url.removePrefix("https://cdn.helius-rpc.com/cdn-cgi/image//https://ipfs.io/ipfs/")
        return "$ipfsGateway$hash"
    }

    return url

}
