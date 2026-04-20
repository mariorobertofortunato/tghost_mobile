package com.mrf.tghost.chain.evm.data.network.http.rpcrequests

import com.solana.rpccore.JsonRpc20Request
import kotlinx.serialization.json.add
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.util.UUID

fun getEvmBalanceRequest(
    address: String
) =
    JsonRpc20Request(
        method = "eth_getBalance",
        params = buildJsonArray {
            add(address)
            add("latest")
        },
        id = UUID.randomUUID().toString()
    )

fun getAlchemyEvmAssetTransfersFromRequest(
    address: String,
    includeInternal: Boolean = true,
    pageKey: String? = null,
): JsonRpc20Request {
    return JsonRpc20Request(
        method = "alchemy_getAssetTransfers",
        params = buildJsonArray {
            addJsonObject {
                put("fromAddress", address)
                put("fromBlock", "0x0")
                put("excludeZeroValue", true)
                put("withMetadata", true)
                put("category", buildJsonArray {
                    add("external")
                    if (includeInternal) add("internal")
                    add("erc20")
                    add("erc721")
                    add("erc1155")
                })
                put("order", "desc")
                put("maxCount", "0xA") //
                pageKey?.takeIf { it.isNotBlank() }?.let { put("pageKey", it) }
            }
        },
        id = UUID.randomUUID().toString(),
    )
}

fun getAlchemyEvmAssetTransfersToRequest(
    address: String,
    includeInternal: Boolean = true,
    pageKey: String? = null,
): JsonRpc20Request {
    return JsonRpc20Request(
        method = "alchemy_getAssetTransfers",
        params = buildJsonArray {
            addJsonObject {
                put("toAddress", address)
                put("fromBlock", "0x0")
                put("excludeZeroValue", true)
                put("withMetadata", true)
                put("category", buildJsonArray {
                    add("external")
                    if (includeInternal) add("internal")
                    add("erc20")
                    add("erc721")
                    add("erc1155")
                })
                put("order", "desc")
                put("maxCount", "0xA") //
                pageKey?.takeIf { it.isNotBlank() }?.let { put("pageKey", it) }
            }
        },
        id = UUID.randomUUID().toString(),
    )
}