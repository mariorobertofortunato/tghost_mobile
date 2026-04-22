package com.mrf.tghost.chain.solana.data.network.http.rpcrequests

import com.mrf.tghost.chain.solana.utils.SOLANA_STAKE_PROGRAM_ID
import com.solana.rpccore.JsonRpc20Request
import kotlinx.serialization.json.add
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.util.UUID

const val COMMITMENT: String = "confirmed"
const val ENCODING_64: String = "base64"

fun getSolanaBalanceRequest(
    publicKey: String
) =
    JsonRpc20Request(
        method = "getBalance",
        params = buildJsonArray {
            add(publicKey)
            add(buildJsonObject {
                put("commitment", COMMITMENT)
            })
        },
        id = UUID.randomUUID().toString()
    )

/** SPL Tokens */
fun getSolanaTokenAccountsRequest(
    publicKey: String,
    programId: String
): JsonRpc20Request {
    return JsonRpc20Request(
        method = "getTokenAccountsByOwner",
        params = buildJsonArray {
            add(publicKey)
            addJsonObject {
                put("programId", programId)
            }
            addJsonObject {
                put("encoding", "jsonParsed")
                put("commitment", COMMITMENT)
            }
        },
        id = UUID.randomUUID().toString()
    )
}

/** SPL Tokens Info */
fun getSolanaAccountInfoRequest(
    accountPubkeyString: String
) = JsonRpc20Request(
    method = "getAccountInfo",
    params = buildJsonArray {
        add(accountPubkeyString)
        addJsonObject {
            put("encoding", ENCODING_64)
            put("commitment", COMMITMENT)
        }
    },
    id = UUID.randomUUID().toString()
)


/** NFTs */
fun getAssetsByOwnerRequest(
    ownerAddress: String,
    page: Int = 1,
    limit: Int = 5
): JsonRpc20Request {
    return JsonRpc20Request(
        method = "getAssetsByOwner",
        params = buildJsonObject {
            put("ownerAddress", ownerAddress)
            //put("page", page)
            //put("limit", limit)
            put("displayOptions", buildJsonObject {
                put("showFungible", true)
                put("showCollectionMetadata", true)
                put("showInscription", true)
            })
        },
        id = UUID.randomUUID().toString()
    )
}

/** Staking */
fun getSolanaStakesRequest(
    walletAddress: String,
    offset: Int = 12 // 12 for Staker, 44 for Withdrawer
): JsonRpc20Request {
    return JsonRpc20Request(
        method = "getProgramAccounts",
        params = buildJsonArray {
            add(SOLANA_STAKE_PROGRAM_ID)
            addJsonObject {
                put("encoding", "jsonParsed")
                put("commitment", COMMITMENT)
                put("filters", buildJsonArray {
                    addJsonObject {
                        put("memcmp", buildJsonObject {
                            put("offset", offset)
                            put("bytes", walletAddress)
                        })
                    }
                })
            }
        },
        id = UUID.randomUUID().toString()
    )
}

/** TX */
fun getSolanaSignaturesForAddress(
    walletAddress: String,
    limit: Int = 100,
    before: String? = null,
    until: String? = null
): JsonRpc20Request {
    val safeLimit = limit.coerceIn(1, 1_000)
    return JsonRpc20Request(
        method = "getSignaturesForAddress",
        params = buildJsonArray {
            add(walletAddress)
            addJsonObject {
                put("commitment", COMMITMENT)
                put("limit", safeLimit)
                before?.takeIf { it.isNotBlank() }?.let { put("before", it) }
                until?.takeIf { it.isNotBlank() }?.let { put("until", it) }
            }
        },
        id = UUID.randomUUID().toString()
    )
}

fun getSolanaTransactionRequest(
    signature: String
): JsonRpc20Request {
    return JsonRpc20Request(
        method = "getTransaction",
        params = buildJsonArray {
            add(signature)
            addJsonObject {
                put("commitment", COMMITMENT)
                put("maxSupportedTransactionVersion", 0)
                put("encoding", "jsonParsed")
            }
        },
        id = UUID.randomUUID().toString()
    )
}
