package com.mrf.tghost.chain.evm.data.network.model.moralis

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class MoralisActivityResponseDto(
    @SerialName("cursor") val cursor: String? = null,
    @SerialName("page") val page: String? = null,
    @SerialName("page_size") val pageSize: String? = null,
    @SerialName("result") val result: List<MoralisActivityItemDto> = emptyList(),
)

@Serializable
data class MoralisActivityItemDto(
    @SerialName("hash") val hash: String,
    @SerialName("block_number") val blockNumber: String? = null,
    @SerialName("block_timestamp") val blockTimestamp: String? = null,
    @SerialName("receipt_status") val receiptStatus: String? = null,
    @SerialName("gas_price") val gasPrice: String? = null,
    @SerialName("receipt_gas_used") val receiptGasUsed: String? = null,
    @SerialName("category") val category: String? = null,
    @SerialName("summary") val summary: String? = null,
    @SerialName("from_address") val fromAddress: String? = null,
    @SerialName("to_address") val toAddress: String? = null,
    @SerialName("erc20_transfers") val erc20Transfers: List<MoralisActivityErc20TransferDto> = emptyList(),
    @SerialName("native_transfers") val nativeTransfers: List<MoralisActivityNativeTransferDto> = emptyList(),
)

@Serializable
data class MoralisActivityErc20TransferDto(
    @SerialName("token_name") val tokenName: String? = null,
    @SerialName("token_symbol") val tokenSymbol: String? = null,
    @SerialName("token_decimals") val tokenDecimals: String? = null,
    @SerialName("address") val tokenAddress: String? = null,
    @SerialName("from_address") val fromAddress: String? = null,
    @SerialName("to_address") val toAddress: String? = null,
    @SerialName("value") val value: JsonElement? = null,
)

@Serializable
data class MoralisActivityNativeTransferDto(
    @SerialName("from_address") val fromAddress: String? = null,
    @SerialName("to_address") val toAddress: String? = null,
    @SerialName("token_symbol") val tokenSymbol: String? = null,
    @SerialName("value") val value: String? = null,
)
