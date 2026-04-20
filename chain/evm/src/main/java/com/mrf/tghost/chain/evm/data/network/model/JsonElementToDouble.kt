package com.mrf.tghost.chain.evm.data.network.model

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.longOrNull

/** Moralis sometimes returns USD fields as JSON numbers, sometimes as strings — accept both. */
fun JsonElement?.toDoubleLenient(): Double? {
    if (this == null || this === JsonNull) return null
    val prim = this as? JsonPrimitive ?: return null
    prim.doubleOrNull?.let { return it }
    prim.longOrNull?.let { return it.toDouble() }
    return prim.content.toDoubleOrNull()
}
