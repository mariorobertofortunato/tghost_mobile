package com.mrf.tghost.chain.sui.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SuiMoveTypeGraphQlDto(
    @SerialName("repr") val repr: String
)

@Serializable
data class SuiPageInfoGraphQlDto(
    @SerialName("hasNextPage") val hasNextPage: Boolean = false,
    @SerialName("endCursor") val endCursor: String? = null
)
