package com.mrf.tghost.domain.model.metadata

data class TokenOffChainMetadata(
    val name: String? = null,
    val symbol: String? = null,
    val description: String? = null,
    val image: String? = null,
    val showName: Boolean? = null,
    val createdOn: String? = null,
    val twitter: String? = null,
    val telegram: String? = null,
    val website: String? = null,
    val attributes: List<OffChainAttribute>? = null,
    val properties: OffChainProperties? = null
)

//@Serializable
data class OffChainAttribute(
    val trait_type: String? = null,
    val value: String? = null
)

//@Serializable
data class OffChainProperties(
    val files: List<OffChainFile>? = null,
    val category: String? = null
)

//@Serializable
data class OffChainFile(
    val uri: String? = null,
    val type: String? = null
)
