package com.mrf.tghost.chain.tezos.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TezosTokenDto(
    @SerialName("token") val token: TezosTokenInfoDto?,
    @SerialName("balance") val balance: String
)

@Serializable
data class TezosTokenInfoDto(
    @SerialName("contract") val contract: TezosContractInfoDto,
    @SerialName("metadata") val metadata: TezosTokenMetadataDto? = null,
    @SerialName("tokenId") val tokenId: String? = null,
    @SerialName("standard") val standard: String? = null,
    @SerialName("totalSupply") val totalSupply: String? = null
)

@Serializable
data class TezosContractInfoDto(
    @SerialName("address") val address: String
)

@Serializable
data class TezosTokenMetadataDto(
    @SerialName("name") val name: String? = null,
    @SerialName("symbol") val symbol: String? = null,
    @SerialName("decimals") val decimals: String? = "0",
    @SerialName("description") val description: String? = null,
    @SerialName("displayUri") val displayUri: String? = null,
    @SerialName("thumbnailUri") val thumbnailUri: String? = null,
    @SerialName("artifactUri") val artifactUri: String? = null
)
