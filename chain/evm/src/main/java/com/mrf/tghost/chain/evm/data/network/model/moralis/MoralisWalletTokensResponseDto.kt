package com.mrf.tghost.chain.evm.data.network.model.moralis

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MoralisWalletTokensResponseDto(
    @SerialName("cursor") val cursor: String? = null,
    @SerialName("page") val page: Int? = null,
    @SerialName("page_size") val pageSize: Int? = null,
    @SerialName("block_number") val blockNumber: Long? = null,
    @SerialName("result") val result: List<MoralisWalletTokenDto> = emptyList(),
)

@Serializable
data class MoralisWalletTokenDto(
    @SerialName("token_address") val tokenAddress: String,
    @SerialName("symbol") val symbol: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("logo") val logo: String? = null,
    @SerialName("thumbnail") val thumbnail: String? = null,
    @SerialName("decimals") val decimals: Int? = null,
    @SerialName("balance") val balance: String? = null,
    @SerialName("native_token") val nativeToken: Boolean? = false,
    @SerialName("usd_price") val usdPrice: Double? = null,
)
