package com.mrf.tghost.chain.solana.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DasApiResponseDto(
    @SerialName("jsonrpc") val jsonrpc: String? = null,
    @SerialName("result") val result: DasResultDto? = null,
    @SerialName("id") val id: String? = null
)

@Serializable
data class DasResultDto(
    @SerialName("last_indexed_slot") val lastIndexedSlot: Long? = null,
    @SerialName("total") val total: Int? = null,
    @SerialName("limit") val limit: Int? = null,
    @SerialName("page") val page: Int? = null,
    @SerialName("items") val items: List<DasItemDto>? = null
)

@Serializable
data class DasItemDto(
    @SerialName("interface") val interfaceName: String? = null,
    @SerialName("id") val id: String? = null,
    @SerialName("content") val content: DasContentDto? = null,
    @SerialName("authorities") val authorities: List<DasAuthorityDto>? = null,
    @SerialName("compression") val compression: DasCompressionDto? = null,
    @SerialName("grouping") val grouping: List<DasGroupingDto>? = null,
    @SerialName("royalty") val royalty: DasRoyaltyDto? = null,
    @SerialName("creators") val creators: List<DasCreatorDto>? = null,
    @SerialName("ownership") val ownership: DasOwnershipDto? = null,
    @SerialName("supply") val supply: DasSupplyDto? = null,
    @SerialName("mutable") val mutable: Boolean? = null,
    @SerialName("burnt") val burnt: Boolean? = null
)

@Serializable
data class DasContentDto(
    @SerialName("\$schema") val schema: String? = null,
    @SerialName("json_uri") val jsonUri: String? = null,
    @SerialName("files") val files: List<DasFileDto>? = null,
    @SerialName("metadata") val metadata: DasMetadataDto? = null
)

@Serializable
data class DasFileDto(
    @SerialName("uri") val uri: String? = null,
    @SerialName("cdn_uri") val cdnUri: String? = null,
    @SerialName("mime") val mime: String? = null
)

@Serializable
data class DasAuthorityDto(
    @SerialName("address") val address: String? = null,
    @SerialName("scopes") val scopes: List<String>? = null
)

@Serializable
data class DasCompressionDto(
    @SerialName("eligible") val eligible: Boolean? = null,
    @SerialName("compressed") val compressed: Boolean? = null,
    @SerialName("data_hash") val dataHash: String? = null,
    @SerialName("creator_hash") val creatorHash: String? = null,
    @SerialName("asset_hash") val assetHash: String? = null,
    @SerialName("tree") val tree: String? = null,
    @SerialName("seq") val seq: Long? = null,
    @SerialName("leaf_id") val leafId: Long? = null
)

@Serializable
data class DasRoyaltyDto(
    @SerialName("royalty_model") val royaltyModel: String? = null,
    @SerialName("target") val target: String? = null,
    @SerialName("percent") val percent: Double? = null,
    @SerialName("basis_points") val basisPoints: Int? = null,
    @SerialName("primary_sale_happened") val primarySaleHappened: Boolean? = null,
    @SerialName("locked") val locked: Boolean? = null
)

@Serializable
data class DasMetadataDto(
    @SerialName("name") val name: String? = null,
    @SerialName("symbol") val symbol: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("creators") val creators: List<DasCreatorDto>? = null
)

@Serializable
data class DasCreatorDto(
    @SerialName("address") val address: String? = null,
    @SerialName("verified") val verified: Boolean = false,
    @SerialName("share") val share: Int? = null
)

@Serializable
data class DasOwnershipDto(
    @SerialName("frozen") val frozen: Boolean? = null,
    @SerialName("delegated") val delegated: Boolean? = null,
    @SerialName("delegate") val delegate: String? = null,
    @SerialName("ownership_model") val ownershipModel: String? = null,
    @SerialName("owner") val owner: String? = null
)

@Serializable
data class DasGroupingDto(
    @SerialName("group_key") val groupKey: String? = null,
    @SerialName("group_value") val groupValue: String? = null
)

@Serializable
data class DasSupplyDto(
    @SerialName("print_max_supply") val printMaxSupply: Long? = null,
    @SerialName("print_current_supply") val printCurrentSupply: Long? = null,
    @SerialName("edition_nonce") val editionNonce: Int? = null
)
