package com.mrf.tghost.chain.sui.domain.model

import kotlinx.serialization.json.JsonElement

data class SuiOwnedObjects(
    val data: List<SuiObject>,
    val nextCursor: String?,
    val hasNextPage: Boolean
)

data class SuiObject(
    val data: SuiObjectData? = null,
    val error: SuiObjectError? = null
)

data class SuiObjectData(
    val objectId: String? = null,
    val version: String? = null,
    val digest: String? = null,
    val type: String? = null,
    val owner: SuiObjectOwner? = null,
    val previousTransaction: String? = null,
    val storageRebate: String? = null,
    val content: SuiMoveObject? = null
)

data class SuiObjectOwner(
    val AddressOwner: String? = null
)

data class SuiMoveObject(
    val dataType: String? = null,
    val type: String? = null,
    val hasPublicTransfer: Boolean,
    val fields: Map<String, JsonElement>
)

data class SuiObjectError(
    val code: String? = null,
    val message: String? = null
)
