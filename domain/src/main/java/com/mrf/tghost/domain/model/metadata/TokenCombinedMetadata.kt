package com.mrf.tghost.domain.model.metadata

import kotlinx.serialization.Serializable

//@Serializable
data class TokenCombinedMetadata (
    val key: UByte? = null,
    val updateAuthority: String? = null,
    val mint: String? = null,
    val name: String? = null,
    val symbol: String? = null,
    val uri: String? = null,
    val sellerFeeBasisPoints: UShort? = null,
    val creators: List<Creator>? = null,
    val primarySaleHappened: Boolean? = null,
    val isMutable: Boolean? = null,
    val editionNonce: UByte? = null,
    val description: String? = null,
    val image: String? = null,
    val showName: Boolean? = null,
    val createdOn: String? = null,
    val twitter: String? = null,
    val telegram: String? = null,
    val website: String? = null,
    val attributes: List<OffChainAttribute>? = null,
    val properties: OffChainProperties? = null
)