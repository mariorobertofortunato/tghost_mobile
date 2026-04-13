package com.mrf.tghost.chain.solana.data.network.websocket

import com.mrf.tghost.chain.solana.data.network.model.DasApiResponseDto
import com.mrf.tghost.chain.solana.data.network.model.SolanaSplTokenAccountDto
import com.mrf.tghost.chain.solana.data.network.model.SolanaStakeAccountDto
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull

private val json = Json { ignoreUnknownKeys = true }

/**
 * Parses a subscription response message. Returns (requestId as string, subscriptionId) or null.
 */
fun parseSolanaSubscriptionResponse(message: String): Pair<String, Long>? {
    return try {
        val obj = json.parseToJsonElement(message).jsonObject
        val id = obj["id"]?.jsonPrimitive?.content
            ?: obj["id"]?.jsonPrimitive?.longOrNull?.toString() ?: return null
        val result = obj["result"]?.jsonPrimitive?.content?.toLongOrNull()
            ?: obj["result"]?.jsonPrimitive?.longOrNull ?: return null
        Pair(id, result)
    } catch (_: Exception) {
        null
    }
}

/**
 * Extracts subscription id from a notification message (accountNotification, programNotification,
 * eth_subscription, or DAS-style with "id")
 */
fun parseSolanaNotificationSubscriptionId(message: String): Long? {
    return try {
        val obj = json.parseToJsonElement(message).jsonObject
        val method = obj["method"]?.jsonPrimitive?.content
        when (method) {
            "accountNotification", "programNotification", "eth_subscription" -> {
                val params = obj["params"]?.jsonObject ?: return null
                val sub = params["subscription"]?.jsonPrimitive
                sub?.longOrNull
                    ?: sub?.content?.toLongOrNull()
                    ?: sub?.content?.removePrefix("0x")?.toLongOrNull(16)
            }
            else -> obj["id"]?.jsonPrimitive?.longOrNull
                ?: obj["id"]?.jsonPrimitive?.content?.toLongOrNull()
        }
    } catch (_: Exception) {
        null
    }
}

/**
 * Extracts the notification "id" (or params.subscription) as string for request-id lookup.
 * Used when our request id is UUID string; server may echo it in notifications.
 */
fun parseSolanaNotificationIdAsString(message: String): String? {
    return try {
        val obj = json.parseToJsonElement(message).jsonObject
        val method = obj["method"]?.jsonPrimitive?.content
        when (method) {
            "accountNotification", "programNotification", "eth_subscription" -> {
                val params = obj["params"]?.jsonObject ?: return null
                val sub = params["subscription"]?.jsonPrimitive
                sub?.content ?: sub?.longOrNull?.toString()
            }
            else -> obj["id"]?.jsonPrimitive?.content
                ?: obj["id"]?.jsonPrimitive?.longOrNull?.toString()
        }
    } catch (_: Exception) {
        null
    }
}

/**
 * wallet lamports notification.
 */
fun parseSolanaAccountNotificationLamports(message: String, subscriptionId: Long? = null): Long? {
    return try {
        val root = json.parseToJsonElement(message)
        val rootObj = root.jsonObject
        val method = rootObj["method"]?.jsonPrimitive?.content
        if (method != "accountNotification" && method != "eth_subscription") return null
        val paramsEl = rootObj["params"] ?: return null
        val params = paramsEl.jsonObject
        if (subscriptionId != null) {
            val subEl = params["subscription"]?.jsonPrimitive ?: return null
            val sub = subEl.longOrNull
                ?: subEl.content?.toLongOrNull()
                ?: subEl.content?.removePrefix("0x")?.toLongOrNull(16)
                ?: return null
            if (sub != subscriptionId) return null
        }
        val resultEl = params["result"] ?: return null
        val resultObj = resultEl.jsonObject
        val valueEl = resultObj["value"] ?: return null
        val valueObj = valueEl.jsonObject
        val lamportsEl = valueObj["lamports"] ?: return null
        lamportsEl.jsonPrimitive.longOrNull
    } catch (_: Exception) {
        null
    }
}

/**
 * token accounts notification
 */
fun parseSolanaProgramNotificationAccount(message: String, subscriptionId: Long): SolanaSplTokenAccountDto? {
    return try {
        val root = json.parseToJsonElement(message).jsonObject
        if (root["method"]?.jsonPrimitive?.content != "programNotification") return null
        val params = root["params"]?.jsonObject ?: return null
        if (params["subscription"]?.jsonPrimitive?.longOrNull != subscriptionId) return null
        val valueEl = params["result"]?.jsonObject?.get("value") ?: return null
        json.decodeFromJsonElement(SolanaSplTokenAccountDto.serializer(), valueEl)
    } catch (_: Exception) {
        null
    }
}

/**
 * stake account notification
 */
fun parseSolanaStakeNotificationAccount(message: String, subscriptionId: Long): SolanaStakeAccountDto? {
    return try {
        val root = json.parseToJsonElement(message).jsonObject
        if (root["method"]?.jsonPrimitive?.content != "programNotification") return null
        val params = root["params"]?.jsonObject ?: return null
        if (params["subscription"]?.jsonPrimitive?.longOrNull != subscriptionId) return null
        val valueEl = params["result"]?.jsonObject?.get("value") ?: return null
        json.decodeFromJsonElement(SolanaStakeAccountDto.serializer(), valueEl)
    } catch (_: Exception) {
        null
    }
}

fun parseSolanaNftNotification(
    message: String,
    requestId: String
): DasApiResponseDto? {
    return try {
        val root = json.parseToJsonElement(message).jsonObject
        val idStr = root["id"]?.jsonPrimitive?.content ?: root["id"]?.jsonPrimitive?.longOrNull?.toString() ?: return null
        if (idStr != requestId) return null
        val result = root["result"]?.jsonObject ?: return null
        json.decodeFromJsonElement(DasApiResponseDto.serializer(), result)
    } catch (e: Exception) {
        null
    }
}