package com.mrf.tghost.chain.solana.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Generic class for RPC response
@Serializable
data class SolanaRpcResponseDto<T>(
    @SerialName("context") val context: RpcContext,
    @SerialName("value") val value: T?
)

@Serializable
data class RpcContext(
    @SerialName("slot") val slot: Long,
    @SerialName("apiVersion") val apiVersion: String? = null
)
