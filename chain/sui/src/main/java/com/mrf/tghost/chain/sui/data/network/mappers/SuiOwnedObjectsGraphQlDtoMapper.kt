package com.mrf.tghost.chain.sui.data.network.mappers

import com.mrf.tghost.chain.sui.data.network.model.SuiOwnedObjectNodeGraphQlDto
import com.mrf.tghost.chain.sui.data.network.model.SuiOwnedObjectsConnectionGraphQlDto
import com.mrf.tghost.chain.sui.domain.model.SuiMoveObject
import com.mrf.tghost.chain.sui.domain.model.SuiObject
import com.mrf.tghost.chain.sui.domain.model.SuiObjectData
import com.mrf.tghost.chain.sui.domain.model.SuiOwnedObjects
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

fun SuiOwnedObjectsConnectionGraphQlDto.toDomainModel(): SuiOwnedObjects {
    return SuiOwnedObjects(
        data = nodes.map { it.toDomainModel() },
        nextCursor = pageInfo?.endCursor,
        hasNextPage = pageInfo?.hasNextPage == true
    )
}

private fun SuiOwnedObjectNodeGraphQlDto.toDomainModel(): SuiObject {
    val moveType = contents?.type?.repr
    val fields = (contents?.json as? JsonObject)?.jsonObject?.toMap().orEmpty()

    return SuiObject(
        data = SuiObjectData(
            objectId = address,
            version = version?.jsonPrimitive?.contentOrNull,
            digest = digest,
            type = moveType,
            content = if (moveType != null || fields.isNotEmpty()) {
                SuiMoveObject(
                    dataType = "moveObject",
                    type = moveType,
                    hasPublicTransfer = hasPublicTransfer,
                    fields = fields
                )
            } else null
        ),
        error = null
    )
}
