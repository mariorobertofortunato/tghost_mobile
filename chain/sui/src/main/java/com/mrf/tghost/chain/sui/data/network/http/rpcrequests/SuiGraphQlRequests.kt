package com.mrf.tghost.chain.sui.data.network.http.rpcrequests

import com.mrf.tghost.data.network.http.model.GraphQlRequest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

fun getSuiOwnedObjectsGraphQlRequest(
    address: String,
    afterCursor: String? = null,
    pageSize: Int = 50
): GraphQlRequest {
    val query = """
        query GetOwnedObjects(${'$'}address: SuiAddress!, ${'$'}first: Int!, ${'$'}after: String) {
          address(address: ${'$'}address) {
            objects(first: ${'$'}first, after: ${'$'}after) {
              pageInfo {
                hasNextPage
                endCursor
              }
              nodes {
                address
                version
                digest
                hasPublicTransfer
                contents {
                  type {
                    repr
                  }
                  display {
                    output
                    errors
                  }
                  json
                }
              }
            }
          }
        }
    """.trimIndent()

    return GraphQlRequest(
        query = query,
        operationName = "GetOwnedObjects",
        variables = buildJsonObject {
            put("address", address)
            put("first", pageSize)
            put("after", afterCursor)
        }
    )
}

fun getSuiWalletActivityGraphQlRequest(
    walletAddress: String,
    afterCursor: String? = null,
    pageSize: Int = 30,
    balanceChangesFirst: Int = 50
): GraphQlRequest {
    val query = """
        query GetWalletActivity(${'$'}address: SuiAddress!, ${'$'}first: Int!, ${'$'}after: String, ${'$'}balanceChangesFirst: Int!) {
          transactions(
            first: ${'$'}first
            after: ${'$'}after
            filter: { affectedAddress: ${'$'}address }
          ) {
            pageInfo {
              hasNextPage
              endCursor
            }
            nodes {
              digest
              sender {
                address
              }
              effects {
                status
                timestamp
                checkpoint {
                  sequenceNumber
                }
                executionError {
                  message
                }
                balanceChanges(first: ${'$'}balanceChangesFirst) {
                  nodes {
                    amount
                    owner {
                      address
                    }
                    coinType {
                      repr
                    }
                  }
                }
              }
            }
          }
        }
    """.trimIndent()

    return GraphQlRequest(
        query = query,
        operationName = "GetWalletActivity",
        variables = buildJsonObject {
            put("address", walletAddress)
            put("first", pageSize)
            put("after", afterCursor)
            put("balanceChangesFirst", balanceChangesFirst)
        }
    )
}

fun getSuiCoinMetadataGraphQlRequest(coinType: String): GraphQlRequest {
    val query = """
        query GetCoinMetadata(${'$'}coinType: String!) {
          coinMetadata(coinType: ${'$'}coinType) {
            address
            decimals
            name
            symbol
            description
            iconUrl
          }
        }
    """.trimIndent()

    return GraphQlRequest(
        query = query,
        operationName = "GetCoinMetadata",
        variables = buildJsonObject {
            put("coinType", coinType)
        }
    )
}
