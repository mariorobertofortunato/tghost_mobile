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

fun getEvmTokenOnChainMetadataRequest(
    contractAddress: String
): JsonRpc20Request {
    return JsonRpc20Request(
        method = "alchemy_getTokenMetadata",
        params = buildJsonArray {
            add(contractAddress)
        },
        id = UUID.randomUUID().toString()
    )
}

fun getEvmTokenAccountsRequest(
    address: String
): JsonRpc20Request {
    return JsonRpc20Request(
        method = "alchemy_getTokenBalances",
        params = buildJsonArray {
            add(address)
            add("erc20")
        },
        id = UUID.randomUUID().toString()
    )
}

fun getNFTsByOwnerRequest(
    owner: String
): JsonRpc20Request {
    return JsonRpc20Request(
        method = "getNFTsForOwner",
        params = buildJsonObject {
            put("owner", owner)
        },
        id = UUID.randomUUID().toString()
    )
}