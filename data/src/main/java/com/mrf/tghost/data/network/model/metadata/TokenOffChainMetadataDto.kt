package com.mrf.tghost.data.network.model.metadata

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TokenOffChainMetadataDto(
    @SerialName("name") val name: String? = null,
    @SerialName("symbol") val symbol: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("image") val image: String? = null,
    @SerialName("showName") val showName: Boolean? = null,
    @SerialName("createdOn") val createdOn: String? = null,
    @SerialName("twitter") val twitter: String? = null,
    @SerialName("telegram") val telegram: String? = null,
    @SerialName("website") val website: String? = null,
    @SerialName("attributes") val attributes: List<OffChainAttributeDto>? = null,
    @SerialName("properties") val properties: OffChainPropertiesDto? = null
)

@Serializable
data class OffChainAttributeDto(
    @SerialName("trait_type") val trait_type: String? = null,
    @SerialName("value") val value: String? = null
)

@Serializable
data class OffChainPropertiesDto(
    @SerialName("files") val files: List<OffChainFileDto>? = null,
    @SerialName("category") val category: String? = null
)

@Serializable
data class OffChainFileDto(
    @SerialName("uri") val uri: String? = null,
    @SerialName("type") val type: String? = null
)
