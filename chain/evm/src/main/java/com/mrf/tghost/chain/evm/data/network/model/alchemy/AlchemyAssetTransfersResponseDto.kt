package com.mrf.tghost.chain.evm.data.network.model.alchemy

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AlchemyAssetTransfersResponseDto(
    @SerialName("transfers") val transfers: List<AlchemyAssetTransferDto> = emptyList(),
    @SerialName("pageKey") val pageKey: String? = null,
)

@Serializable
data class AlchemyAssetTransferDto(
    @SerialName("blockNum") val blockNum: String? = null,
    @SerialName("uniqueId") val uniqueId: String? = null,
    @SerialName("hash") val hash: String? = null,
    @SerialName("from") val from: String? = null,
    @SerialName("to") val to: String? = null,
    @SerialName("value") val value: Double? = null,
    @SerialName("asset") val asset: String? = null,
    @SerialName("category") val category: String? = null,
    @SerialName("rawContract") val rawContract: AlchemyRawContractDto? = null,
    @SerialName("metadata") val metadata: AlchemyTransferMetadataDto? = null,
    @SerialName("logIndex") val logIndex: String? = null,
)

@Serializable
data class AlchemyRawContractDto(
    @SerialName("value") val value: String? = null,
    @SerialName("address") val address: String? = null,
    @SerialName("decimal") val decimal: String? = null,
)

@Serializable
data class AlchemyTransferMetadataDto(
    @SerialName("blockTimestamp") val blockTimestamp: String? = null,
)
