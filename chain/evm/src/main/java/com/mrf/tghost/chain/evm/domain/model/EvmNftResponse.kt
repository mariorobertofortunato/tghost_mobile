package com.mrf.tghost.chain.evm.domain.model

data class EvmNftResponse(
    val status: String? = null,
    val page: Int? = null,
    val pageSize: Int? = null,
    val cursor: String? = null,
    val result: List<EvmNftResult>? = null
)

data class EvmNftResult(
    val amount: String? = null,
    val tokenId: String? = null,
    val tokenAddress: String? = null,
    val contractType: String? = null,
    val ownerOf: String? = null,
    val lastMetadataSync: String? = null,
    val lastTokenUriSync: String? = null,
    val metadata: String? = null,
    val blockNumber: String? = null,
    val blockNumberMinted: String? = null,
    val name: String? = null,
    val symbol: String? = null,
    val tokenHash: String? = null,
    val tokenUri: String? = null,
    val minterAddress: String? = null,
    val rarityRank: Int? = null,
    val rarityPercentage: Double? = null,
    val rarityLabel: String? = null,
    val verifiedCollection: Boolean? = null,
    val possibleSpam: Boolean? = null,
    val normalizedMetadata: EvmNftNormalizedMetadata? = null,
    val collectionLogo: String? = null,
    val collectionBannerImage: String? = null,
    val collectionCategory: String? = null,
    val projectUrl: String? = null,
    val wikiUrl: String? = null,
    val discordUrl: String? = null,
    val telegramUrl: String? = null,
    val twitterUsername: String? = null,
    val instagramUsername: String? = null,
    val listPrice: EvmNftListPrice? = null,
    val floorPrice: String? = null,
    val floorPriceUsd: String? = null,
    val floorPriceCurrency: String? = null
)

data class EvmNftNormalizedMetadata(
    val name: String? = null,
    val description: String? = null,
    val animationUrl: String? = null,
    val externalLink: String? = null,
    val externalUrl: String? = null,
    val image: String? = null,
    // val attributes: List<Attribute>? = null
)

data class EvmNftListPrice(
    val listed: Boolean? = null,
    val price: String? = null,
    val priceCurrency: String? = null,
    val priceUsd: String? = null,
    val marketplace: String? = null
)
