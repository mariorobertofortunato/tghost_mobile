package com.mrf.tghost.data.network.model.metadata

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TokenOnChainMetadataDto(
    @SerialName("key") val key: UByte,
    @SerialName("updateAuthority") val updateAuthority: String,
    @SerialName("mint") val mint: String,
    @SerialName("name") val name: String,
    @SerialName("symbol") val symbol: String,
    @SerialName("uri") val uri: String,
    @SerialName("sellerFeeBasisPoints") val sellerFeeBasisPoints: UShort,
    @SerialName("creators") val creators: List<CreatorDto>? = null,
    @SerialName("primarySaleHappened") val primarySaleHappened: Boolean? = null,
    @SerialName("isMutable") val isMutable: Boolean? = null,
    @SerialName("editionNonce") val editionNonce: UByte? = null
)

@Serializable
data class CreatorDto(
    @SerialName("address") val address: String,
    @SerialName("verified") val verified: Boolean,
    @SerialName("share") val share: UByte
)
