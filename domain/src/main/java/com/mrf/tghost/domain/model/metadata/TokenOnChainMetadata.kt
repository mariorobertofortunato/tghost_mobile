package com.mrf.tghost.domain.model.metadata

import kotlinx.serialization.Serializable

data class TokenOnChainMetadata(
    val key: UByte?,
    val updateAuthority: String?,
    val mint: String?,
    val name: String,
    val symbol: String,
    val uri: String,
    val sellerFeeBasisPoints: UShort?,
    val creators: List<Creator>?,
    val primarySaleHappened: Boolean?,
    val isMutable: Boolean?,
    val editionNonce: UByte? = null
)

//@Serializable
data class Creator(
    val address: String,
    val verified: Boolean,
    val share: UByte
)
