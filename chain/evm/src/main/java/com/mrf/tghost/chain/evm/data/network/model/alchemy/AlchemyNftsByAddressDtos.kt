package com.mrf.tghost.chain.evm.data.network.model.alchemy

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

// --- Request (Portfolio NFTs by address)

@Serializable
data class AlchemyNftsByAddressRequestDto(
    val addresses: List<AlchemyNftByAddressAddressEntryDto>,
    val withMetadata: Boolean = true,
    //val pageKey: String? = null,
    //val pageSize: Int = 100,
    val orderBy: String? = "transferTime",
    val sortOrder: String? = "asc",
)

@Serializable
data class AlchemyNftByAddressAddressEntryDto(
    val address: String,
    val networks: List<String>,
    val excludeFilters: List<String>? = null,
    val includeFilters: List<String>? = null,
    val spamConfidenceLevel: String? = null,
)

// --- Response

@Serializable
data class AlchemyNftsByAddressResponseDto(
    @SerialName("data") val data: AlchemyNftsByAddressDataDto? = null,
)

@Serializable
data class AlchemyNftsByAddressDataDto(
    @SerialName("ownedNfts") val ownedNfts: List<AlchemyOwnedNftDto> = emptyList(),
    @SerialName("totalCount") val totalCount: Int? = null,
    @SerialName("pageKey") val pageKey: String? = null,
)

@Serializable
data class AlchemyOwnedNftDto(
    @SerialName("network") val network: String? = null,
    @SerialName("address") val address: String? = null,
    @SerialName("balance") val balance: String? = null,
    @SerialName("contract") val contract: AlchemyNftContractDto? = null,
    @SerialName("tokenId") val tokenId: String? = null,
    @SerialName("tokenType") val tokenType: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("image") val image: AlchemyNftImageDto? = null,
    @SerialName("raw") val raw: AlchemyNftRawDto? = null,
    @SerialName("collection") val collection: AlchemyNftCollectionDto? = null,
    @SerialName("tokenUri") val tokenUri: String? = null,
    @SerialName("timeLastUpdated") val timeLastUpdated: String? = null,
    @SerialName("acquiredAt") val acquiredAt: AlchemyNftAcquiredAtDto? = null,
)

@Serializable
data class AlchemyNftContractDto(
    @SerialName("address") val address: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("symbol") val symbol: String? = null,
    @SerialName("totalSupply") val totalSupply: String? = null,
    @SerialName("tokenType") val tokenType: String? = null,
    @SerialName("contractDeployer") val contractDeployer: String? = null,
    @SerialName("deployedBlockNumber") val deployedBlockNumber: Long? = null,
    @SerialName("openSeaMetadata") val openSeaMetadata: AlchemyNftOpenseaMetadataDto? = null,
    @SerialName("isSpam") val isSpam: Boolean? = null,
    @SerialName("spamClassifications") val spamClassifications: List<String> = emptyList(),
)

@Serializable
data class AlchemyNftOpenseaMetadataDto(
    @SerialName("floorPrice") val floorPrice: Double? = null,
    @SerialName("collectionName") val collectionName: String? = null,
    @SerialName("safelistRequestStatus") val safelistRequestStatus: String? = null,
    @SerialName("imageUrl") val imageUrl: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("externalUrl") val externalUrl: String? = null,
    @SerialName("twitterUsername") val twitterUsername: String? = null,
    @SerialName("discordUrl") val discordUrl: String? = null,
    @SerialName("bannerImageUrl") val bannerImageUrl: String? = null,
    @SerialName("lastIngestedAt") val lastIngestedAt: String? = null,
)

@Serializable
data class AlchemyNftImageDto(
    @SerialName("cachedUrl") val cachedUrl: String? = null,
    @SerialName("thumbnailUrl") val thumbnailUrl: String? = null,
    @SerialName("pngUrl") val pngUrl: String? = null,
    @SerialName("contentType") val contentType: String? = null,
    @SerialName("size") val size: Long? = null,
    @SerialName("originalUrl") val originalUrl: String? = null,
)

@Serializable
data class AlchemyNftRawDto(
    @SerialName("tokenUri") val tokenUri: String? = null,
    @SerialName("metadata") val metadata: JsonElement? = null,
    @SerialName("error") val error: String? = null,
)

@Serializable
data class AlchemyNftRawMetadataDto(
    @SerialName("image") val image: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("external_url") val externalUrl: String? = null,
    @SerialName("animation_url") val animationUrl: String? = null,
    @SerialName("attributes") val attributes: List<AlchemyNftAttributeDto> = emptyList(),
)

@Serializable
data class AlchemyNftAttributeDto(
    @SerialName("value") val value: String? = null,
    @SerialName("trait_type") val traitType: String? = null,
)

@Serializable
data class AlchemyNftCollectionDto(
    @SerialName("name") val name: String? = null,
    @SerialName("slug") val slug: String? = null,
    @SerialName("externalUrl") val externalUrl: String? = null,
    @SerialName("bannerImageUrl") val bannerImageUrl: String? = null,
)

@Serializable
data class AlchemyNftAcquiredAtDto(
    @SerialName("blockTimestamp") val blockTimestamp: String? = null,
    @SerialName("blockNumber") val blockNumber: Long? = null,
)
