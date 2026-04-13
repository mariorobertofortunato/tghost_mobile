package com.mrf.tghost.chain.evm.data.network.model.alchemy

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EvmMetadataResponseDto(
    @SerialName("name")     val name: String? = null,
    @SerialName("symbol")   val symbol: String? = null,
    @SerialName("decimals") val decimals: Int? = null,
    @SerialName("logo")     val logo: String? = null,
)
