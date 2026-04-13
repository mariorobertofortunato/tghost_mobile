package com.mrf.tghost.chain.sui.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SuiCoinMetadataGraphQlDataDto(
    @SerialName("coinMetadata") val coinMetadata: SuiCoinMetadataGraphQlDto? = null
)

@Serializable
data class SuiCoinMetadataGraphQlDto(
    @SerialName("address") val address: String,
    @SerialName("decimals") val decimals: Int,
    @SerialName("name") val name: String,
    @SerialName("symbol") val symbol: String,
    @SerialName("description") val description: String? = null,
    @SerialName("iconUrl") val iconUrl: String? = null
)
