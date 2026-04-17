package com.mrf.tghost.chain.evm.data.network.model.moralis

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MoralisEvmNftDto(
    @SerialName("status") val status: String? = null,
    /** Moralis may return string or int depending on endpoint version. */
    @SerialName("page") val page: String? = null,
    @SerialName("page_size") val pageSize: String? = null,
    @SerialName("cursor") val cursor: String? = null,
    @SerialName("result") val result: List<MoralisEvmNftResultDto>? = null,
)

@Serializable
data class MoralisEvmNftResultDto(
    @SerialName("amount") val amount: String? = null,
    @SerialName("token_id") val tokenId: String? = null,
    @SerialName("token_address") val tokenAddress: String? = null,
    @SerialName("contract_type") val contractType: String? = null,
    @SerialName("owner_of") val ownerOf: String? = null,
    @SerialName("last_metadata_sync") val lastMetadataSync: String? = null,
    @SerialName("last_token_uri_sync") val lastTokenUriSync: String? = null,
    @SerialName("metadata") val metadata: String? = null,
    @SerialName("block_number") val blockNumber: String? = null,
    @SerialName("block_number_minted") val blockNumberMinted: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("symbol") val symbol: String? = null,
    @SerialName("token_hash") val tokenHash: String? = null,
    @SerialName("token_uri") val tokenUri: String? = null,
    @SerialName("minter_address") val minterAddress: String? = null,
    @SerialName("rarity_rank") val rarityRank: Int? = null,
    @SerialName("rarity_percentage") val rarityPercentage: Double? = null,
    @SerialName("rarity_label") val rarityLabel: String? = null,
    /** API may return `"true"` / `"false"` strings. */
    @SerialName("verified_collection") val verifiedCollection: String? = null,
    @SerialName("possible_spam") val possibleSpam: String? = null,
    @SerialName("normalized_metadata") val normalizedMetadata: MoralisEvmNftNormalizedMetadataDto? = null,
    @SerialName("collection_logo") val collectionLogo: String? = null,
    @SerialName("collection_banner_image") val collectionBannerImage: String? = null,
    @SerialName("collection_category") val collectionCategory: String? = null,
    @SerialName("project_url") val projectUrl: String? = null,
    @SerialName("wiki_url") val wikiUrl: String? = null,
    @SerialName("discord_url") val discordUrl: String? = null,
    @SerialName("telegram_url") val telegramUrl: String? = null,
    @SerialName("twitter_username") val twitterUsername: String? = null,
    @SerialName("instagram_username") val instagramUsername: String? = null,
    @SerialName("list_price") val listPrice: MoralisEvmNftListPriceDto? = null,
    @SerialName("floor_price") val floorPrice: String? = null,
    @SerialName("floor_price_usd") val floorPriceUsd: String? = null,
    @SerialName("floor_price_currency") val floorPriceCurrency: String? = null,
    @SerialName("last_sale") val lastSale: MoralisEvmNftLastSaleDto? = null,
    @SerialName("media") val media: MoralisEvmNftMediaDto? = null,
)

@Serializable
data class MoralisEvmNftNormalizedMetadataDto(
    @SerialName("name") val name: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("animation_url") val animationUrl: String? = null,
    @SerialName("external_link") val externalLink: String? = null,
    @SerialName("external_url") val externalUrl: String? = null,
    @SerialName("image") val image: String? = null,
    @SerialName("attributes") val attributes: List<MoralisEvmNftAttributeDto> = emptyList(),
)

@Serializable
data class MoralisEvmNftAttributeDto(
    @SerialName("trait_type") val traitType: String? = null,
    @SerialName("value") val value: String? = null,
    @SerialName("display_type") val displayType: String? = null,
    @SerialName("max_value") val maxValue: Int? = null,
    @SerialName("trait_count") val traitCount: Int? = null,
    @SerialName("order") val order: Int? = null,
)

@Serializable
data class MoralisEvmNftListPriceDto(
    @SerialName("listed") val listed: Boolean? = null,
    @SerialName("price") val price: String? = null,
    @SerialName("price_currency") val priceCurrency: String? = null,
    @SerialName("price_usd") val priceUsd: String? = null,
    @SerialName("marketplace") val marketplace: String? = null,
)

@Serializable
data class MoralisEvmNftLastSaleDto(
    @SerialName("transaction_hash") val transactionHash: String? = null,
    @SerialName("block_timestamp") val blockTimestamp: String? = null,
    @SerialName("buyer_address") val buyerAddress: String? = null,
    @SerialName("seller_address") val sellerAddress: String? = null,
    @SerialName("price") val price: String? = null,
    @SerialName("price_formatted") val priceFormatted: String? = null,
    @SerialName("payment_token") val paymentToken: MoralisEvmNftPaymentTokenDto? = null,
    @SerialName("usd_price_at_sale") val usdPriceAtSale: String? = null,
    @SerialName("current_usd_value") val currentUsdValue: String? = null,
    @SerialName("token_address") val tokenAddress: String? = null,
    @SerialName("token_id") val tokenId: String? = null,
)

@Serializable
data class MoralisEvmNftPaymentTokenDto(
    @SerialName("token_name") val tokenName: String? = null,
    @SerialName("token_symbol") val tokenSymbol: String? = null,
    @SerialName("token_logo") val tokenLogo: String? = null,
    @SerialName("token_decimals") val tokenDecimals: String? = null,
    @SerialName("token_address") val tokenAddress: String? = null,
)

@Serializable
data class MoralisEvmNftMediaDto(
    @SerialName("mimetype") val mimetype: String? = null,
    @SerialName("category") val category: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("original_media_url") val originalMediaUrl: String? = null,
    @SerialName("updatedAt") val updatedAt: String? = null,
    @SerialName("parent_hash") val parentHash: String? = null,
    @SerialName("media_collection") val mediaCollection: MoralisEvmNftMediaCollectionDto? = null,
)

@Serializable
data class MoralisEvmNftMediaCollectionDto(
    @SerialName("low") val low: MoralisEvmNftMediaVariantDto? = null,
    @SerialName("medium") val medium: MoralisEvmNftMediaVariantDto? = null,
    @SerialName("high") val high: MoralisEvmNftMediaVariantDto? = null,
)

@Serializable
data class MoralisEvmNftMediaVariantDto(
    @SerialName("width") val width: Int? = null,
    @SerialName("height") val height: Int? = null,
    @SerialName("url") val url: String? = null,
)
