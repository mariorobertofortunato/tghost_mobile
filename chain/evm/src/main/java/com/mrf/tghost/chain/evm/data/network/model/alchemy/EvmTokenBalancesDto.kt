package com.mrf.tghost.chain.evm.data.network.model.alchemy

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EvmTokenBalancesDto(
    @SerialName("address")          val address: String,
    @SerialName("tokenBalances")    val tokenBalances: List<EvmTokenBalanceDto>
)

@Serializable
data class EvmTokenBalanceDto(
    @SerialName("contractAddress")  val contractAddress: String,
    @SerialName("tokenBalance")     val tokenBalance: String? = null, // Hex string like "0x..."
    @SerialName("error")            val error: String? = null
)


