package com.mrf.tghost.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class TokenQuoteDto(
    @SerialName("inputMint") val inputMint: String,
    @SerialName("inAmount") val inAmount: String,
    @SerialName("outputMint") val outputMint: String,
    @SerialName("outAmount") val outAmount: String,
    @SerialName("otherAmountThreshold") val otherAmountThreshold: String,
    @SerialName("swapMode") val swapMode: String,
    @SerialName("slippageBps") val slippageBps: Int,
    @SerialName("platformFee") val platformFee: JsonElement? = null,
    @SerialName("priceImpactPct") val priceImpactPct: String,
    @SerialName("routePlan") val routePlan: List<RoutePlanStep>,
    @SerialName("contextSlot") val contextSlot: Long,
    @SerialName("timeTaken") val timeTaken: Double,
    @SerialName("swapUsdValue") val swapUsdValue: String,
    @SerialName("simplerRouteUsed") val simplerRouteUsed: Boolean? = null,
    @SerialName("mostReliableAmmsQuoteReport") val mostReliableAmmsQuoteReport: MostReliableAmmsQuoteReport? = null,
    @SerialName("useIncurredSlippageForQuoting") val useIncurredSlippageForQuoting: Boolean? = null,
    @SerialName("otherRoutePlans") val otherRoutePlans: List<RoutePlanStep>? = null,
    @SerialName("aggregatorVersion") val aggregatorVersion: String? = null
)

@Serializable
data class PlatformFee(
    @SerialName("_ignore") val _ignore: String? = null
)


@Serializable
data class RoutePlanStep(
    @SerialName("swapInfo") val swapInfo: SwapInfo? = null,
    @SerialName("percent") val percent: Int? = null,
    @SerialName("bps") val bps: Int? = null
)

@Serializable
data class SwapInfo(
    @SerialName("ammKey") val ammKey: String? = null,
    @SerialName("label") val label: String? = null,
    @SerialName("inputMint") val inputMint: String? = null,
    @SerialName("outputMint") val outputMint: String? = null,
    @SerialName("inAmount") val inAmount: String? = null,
    @SerialName("outAmount") val outAmount: String? = null,
    @SerialName("feeAmount") val feeAmount: String? = null,
    @SerialName("feeMint") val feeMint: String? = null
)

@Serializable
data class MostReliableAmmsQuoteReport(
    @SerialName("info") val info: Map<String, String>? = null
)
