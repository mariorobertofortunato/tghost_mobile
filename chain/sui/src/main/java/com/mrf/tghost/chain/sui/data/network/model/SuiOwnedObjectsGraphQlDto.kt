package com.mrf.tghost.chain.sui.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class SuiOwnedObjectsGraphQlDataDto(
    @SerialName("address") val address: SuiOwnedObjectsAddressGraphQlDto? = null
)

@Serializable
data class SuiOwnedObjectsAddressGraphQlDto(
    @SerialName("objects") val objects: SuiOwnedObjectsConnectionGraphQlDto? = null
)

@Serializable
data class SuiOwnedObjectsConnectionGraphQlDto(
    @SerialName("nodes") val nodes: List<SuiOwnedObjectNodeGraphQlDto> = emptyList(),
    @SerialName("pageInfo") val pageInfo: SuiPageInfoGraphQlDto? = null
)

@Serializable
data class SuiOwnedObjectNodeGraphQlDto(
    @SerialName("address") val address: String? = null,
    @SerialName("version") val version: JsonElement? = null,
    @SerialName("digest") val digest: String? = null,
    @SerialName("hasPublicTransfer") val hasPublicTransfer: Boolean = false,
    @SerialName("contents") val contents: SuiMoveContentsGraphQlDto? = null
)

@Serializable
data class SuiMoveContentsGraphQlDto(
    @SerialName("type") val type: SuiMoveTypeGraphQlDto? = null,
    @SerialName("display") val display: SuiRenderedDisplayGraphQlDto? = null,
    @SerialName("json") val json: JsonElement? = null
)

@Serializable
data class SuiRenderedDisplayGraphQlDto(
    @SerialName("output") val output: JsonElement? = null,
    @SerialName("errors") val errors: JsonElement? = null
)
