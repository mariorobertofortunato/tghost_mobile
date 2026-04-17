package com.mrf.tghost.chain.evm.data.network.model.alchemy

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AlchemyTokensByAddressResponseDto(
    @SerialName("data") val data: AlchemyTokensByAddressDataDto? = null,
)

@Serializable
data class AlchemyTokensByAddressDataDto(
    @SerialName("tokens") val tokens: List<AlchemyTokenDto> = emptyList(),
    @SerialName("pageKey") val pageKey: String? = null,
)

@Serializable
data class AlchemyTokenDto(
    @SerialName("address") val ownerAddress: String? = null,
    @SerialName("network") val network: String? = null,
    @SerialName("tokenAddress") val tokenAddress: String? = null,
    @SerialName("tokenBalance") val tokenBalance: String? = null,
    @SerialName("tokenMetadata") val tokenMetadata: AlchemyTokenMetadataDto? = null,
    @SerialName("tokenPrices") val tokenPrices: List<AlchemyTokenPriceDto> = emptyList(),
)

@Serializable
data class AlchemyTokenMetadataDto(
    @SerialName("decimals") val decimals: Int? = null,
    @SerialName("logo") val logo: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("symbol") val symbol: String? = null,
)

@Serializable
data class AlchemyTokenPriceDto(
    @SerialName("currency") val currency: String? = null,
    @SerialName("value") val value: String? = null,
    @SerialName("lastUpdatedAt") val lastUpdatedAt: String? = null,
)
