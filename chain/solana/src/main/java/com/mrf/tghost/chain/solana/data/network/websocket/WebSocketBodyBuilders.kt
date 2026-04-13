package com.mrf.tghost.chain.solana.data.network.websocket

import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.util.UUID

const val COMMITMENT: String = "confirmed"

fun buildSolanaAccountSubscribeBody(publicKey: String): Pair<String, String> {
    val requestId = UUID.randomUUID().toString()
    val body = buildJsonObject {
        put("jsonrpc", "2.0")
        put("method", "accountSubscribe")
        put(
            "params",
            buildJsonArray {
                add(publicKey)
                add(
                    buildJsonObject {
                        put("encoding", "base64")
                        put("commitment", COMMITMENT)
                    }
                )
            }
        )
        put("id", requestId)
    }.toString()
    return Pair(body, requestId)
}

fun buildSolanaProgramSubscribeBody(programId: String, ownerBase58: String): Pair<String, String> {
    val requestId = UUID.randomUUID().toString()
    val body = buildJsonObject {
        put("jsonrpc", "2.0")
        put("method", "programSubscribe")
        put(
            "params",
            buildJsonArray {
                add(programId)
                add(
                    buildJsonObject {
                        put("encoding", "jsonParsed")
                        put("commitment", COMMITMENT)
                        put(
                            "filters",
                            buildJsonArray {
                                add(
                                    buildJsonObject {
                                        put(
                                            "memcmp",
                                            buildJsonObject {
                                                put("offset", 32)
                                                put("bytes", ownerBase58)
                                            }
                                        )
                                    }
                                )
                            }
                        )
                    }
                )
            }
        )
        put("id", requestId)
    }.toString()
    return Pair(body, requestId)
}

fun buildSolanaNftSubscribeBody(ownerBase58: String): Pair<String, String> {
    val requestId = UUID.randomUUID().toString()
    val body = buildJsonObject {
        put("jsonrpc", "2.0")
        put("id", requestId)
        put("method", "getAssetsByOwner")
        put("params", buildJsonObject {
            put("ownerAddress", ownerBase58)
            put("page", 1)
        })
    }.toString()

    return Pair(body, requestId)
}