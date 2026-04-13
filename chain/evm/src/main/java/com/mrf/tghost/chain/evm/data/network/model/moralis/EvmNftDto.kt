package com.mrf.tghost.chain.evm.data.network.model.moralis

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EvmNftDto(
    @SerialName("status") val status: String? = null,
    @SerialName("page") val page: Int? = null,
    @SerialName("page_size") val pageSize: Int? = null,
    @SerialName("cursor") val cursor: String? = null,
    @SerialName("result") val result: List<EvmNftResultDto>? = null
)

@Serializable
data class EvmNftResultDto(
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
    @SerialName("verified_collection") val verifiedCollection: Boolean? = null,
    @SerialName("possible_spam") val possibleSpam: Boolean? = null,
    @SerialName("normalized_metadata") val normalizedMetadata: EvmNftNormalizedMetadataDto? = null,
    @SerialName("collection_logo") val collectionLogo: String? = null,
    @SerialName("collection_banner_image") val collectionBannerImage: String? = null,
    @SerialName("collection_category") val collectionCategory: String? = null,
    @SerialName("project_url") val projectUrl: String? = null,
    @SerialName("wiki_url") val wikiUrl: String? = null,
    @SerialName("discord_url") val discordUrl: String? = null,
    @SerialName("telegram_url") val telegramUrl: String? = null,
    @SerialName("twitter_username") val twitterUsername: String? = null,
    @SerialName("instagram_username") val instagramUsername: String? = null,
    @SerialName("list_price") val listPrice: EvmNftListPriceDto? = null,
    @SerialName("floor_price") val floorPrice: String? = null,
    @SerialName("floor_price_usd") val floorPriceUsd: String? = null,
    @SerialName("floor_price_currency") val floorPriceCurrency: String? = null
)

@Serializable
data class EvmNftNormalizedMetadataDto(
    @SerialName("name") val name: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("animation_url") val animationUrl: String? = null,
    @SerialName("external_link") val externalLink: String? = null,
    @SerialName("external_url") val externalUrl: String? = null,
    @SerialName("image") val image: String? = null,
    // @SerialName("attributes") val attributes: List<Attribute>? = null
)

@Serializable
data class EvmNftListPriceDto(
    @SerialName("listed") val listed: Boolean? = null,
    @SerialName("price") val price: String? = null,
    @SerialName("price_currency") val priceCurrency: String? = null,
    @SerialName("price_usd") val priceUsd: String? = null,
    @SerialName("marketplace") val marketplace: String? = null
)