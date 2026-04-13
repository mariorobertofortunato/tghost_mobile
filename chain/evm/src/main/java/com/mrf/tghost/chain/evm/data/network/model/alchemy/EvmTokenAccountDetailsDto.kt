package com.mrf.tghost.chain.evm.data.network.model.alchemy

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EvmTokenAccountDetailsDto(
    @SerialName("contractAddress")  val contractAddress: String,
    @SerialName("name")             val name: String? = null,
    @SerialName("decimals")         val decimals: Int? = null,
    @SerialName("symbol")           val symbol: String? = null,
    @SerialName("balance")          val balance: String? = null
)