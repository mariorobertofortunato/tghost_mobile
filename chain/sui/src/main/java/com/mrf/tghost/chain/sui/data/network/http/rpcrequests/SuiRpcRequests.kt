package com.mrf.tghost.chain.sui.data.network.http.rpcrequests

import com.solana.rpccore.JsonRpc20Request
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import java.util.UUID

fun getSuiStakesRequest(
    address: String
): JsonRpc20Request {
    return JsonRpc20Request(
        method = "suix_getStakes",
        params = buildJsonArray {
            add(address)
        },
        id = UUID.randomUUID().toString()
    )
}
