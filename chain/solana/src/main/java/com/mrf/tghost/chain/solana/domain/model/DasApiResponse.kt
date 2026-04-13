package com.mrf.tghost.chain.solana.domain.model

data class DasApiResponse(
    val jsonrpc: String? = null,
    val result: DasResult? = null,
    val id: String? = null
)

data class DasResult(
    val lastIndexedSlot: Long? = null,
    val total: Int? = null,
    val limit: Int? = null,
    val page: Int? = null,
    val items: List<DasItem>? = null
)

data class DasItem(
    val interfaceName: String? = null,
    val id: String? = null,
    val content: DasContent? = null,
    val authorities: List<DasAuthority>? = null,
    val compression: DasCompression? = null,
    val grouping: List<DasGrouping>? = null,
    val royalty: DasRoyalty? = null,
    val creators: List<DasCreator>? = null,
    val ownership: DasOwnership? = null,
    val supply: DasSupply? = null,
    val mutable: Boolean? = null,
    val burnt: Boolean? = null
)

data class DasContent(
    val schema: String? = null,
    val jsonUri: String? = null,
    val files: List<DasFile>? = null,
    val metadata: DasMetadata? = null
)

data class DasFile(
    val uri: String? = null,
    val cdnUri: String? = null,
    val mime: String? = null
)

data class DasAuthority(
    val address: String? = null,
    val scopes: List<String>? = null
)

data class DasCompression(
    val eligible: Boolean? = null,
    val compressed: Boolean? = null,
    val dataHash: String? = null,
    val creatorHash: String? = null,
    val assetHash: String? = null,
    val tree: String? = null,
    val seq: Long? = null,
    val leafId: Long? = null
)

data class DasRoyalty(
    val royaltyModel: String? = null,
    val target: String? = null,
    val percent: Double? = null,
    val basisPoints: Int? = null,
    val primarySaleHappened: Boolean? = null,
    val locked: Boolean? = null
)

data class DasMetadata(
    val name: String? = null,
    val symbol: String? = null,
    val description: String? = null,
    val creators: List<DasCreator>? = null
)

data class DasCreator(
    val address: String? = null,
    val verified: Boolean = false,
    val share: Int? = null
)

data class DasOwnership(
    val frozen: Boolean? = null,
    val delegated: Boolean? = null,
    val delegate: String? = null,
    val ownershipModel: String? = null,
    val owner: String? = null
)

data class DasGrouping(
    val groupKey: String? = null,
    val groupValue: String? = null
)

data class DasSupply(
    val printMaxSupply: Long? = null,
    val printCurrentSupply: Long? = null,
    val editionNonce: Int? = null
)
