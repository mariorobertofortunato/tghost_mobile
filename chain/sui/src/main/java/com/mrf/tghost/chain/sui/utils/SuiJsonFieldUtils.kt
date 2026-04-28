package com.mrf.tghost.chain.sui.utils

import java.math.BigDecimal
import java.math.BigInteger
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.longOrNull

internal fun flattenMoveObjectFields(fields: Map<String, JsonElement>): Map<String, JsonElement> {
    val inner = (fields["fields"] as? JsonObject)?.jsonObject?.toMap().orEmpty()
    if (inner.isEmpty()) return fields
    return inner + fields.filterKeys { it != "fields" }
}

internal fun Map<String, JsonElement>.getString(vararg keys: String): String? {
    for (key in keys) {
        val el = this[key] ?: continue
        if (el is JsonNull) continue
        val prim = el as? JsonPrimitive ?: continue
        val text = prim.contentOrNull?.trim()?.takeIf { it.isNotEmpty() } ?: continue
        return text
    }
    return null
}

internal fun Map<String, JsonElement>.getStringDeep(vararg keys: String): String? {
    getString(*keys)?.let { return it }
    val wanted = keys.toSet()
    val stack = ArrayDeque<JsonElement>()
    values.forEach { stack.addLast(it) }
    while (stack.isNotEmpty()) {
        when (val node = stack.removeFirst()) {
            is JsonObject -> {
                for ((k, v) in node) {
                    if (k in wanted) {
                        val primitive = v as? JsonPrimitive
                        val text = primitive?.contentOrNull?.trim()
                        if (!text.isNullOrBlank()) return text
                    }
                    stack.addLast(v)
                }
            }
            is JsonArray -> node.forEach { stack.addLast(it) }
            else -> Unit
        }
    }
    return null
}

internal fun JsonElement.graphQlBigIntToBigDecimal(): BigDecimal? =
    when (this) {
        is JsonPrimitive ->
            contentOrNull?.toBigDecimalOrNull() ?: longOrNull?.toBigDecimal()
        else -> null
    }

internal fun JsonElement.asBigIntegerOrNull(): BigInteger? {
    val p = this as? JsonPrimitive ?: return null
    return p.content.toBigIntegerOrNull()
        ?: p.content.toLongOrNull()?.toBigInteger()
}
