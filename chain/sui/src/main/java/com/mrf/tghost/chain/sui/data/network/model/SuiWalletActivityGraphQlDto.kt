package com.mrf.tghost.chain.sui.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SuiWalletActivityGraphQlDataDto(
    @SerialName("transactions") val transactions: SuiTransactionsConnectionGraphQlDto? = null
)

@Serializable
data class SuiTransactionsConnectionGraphQlDto(
    @SerialName("nodes") val nodes: List<SuiTransactionNodeGraphQlDto> = emptyList(),
    @SerialName("pageInfo") val pageInfo: SuiPageInfoGraphQlDto? = null
)

@Serializable
data class SuiTransactionNodeGraphQlDto(
    @SerialName("digest") val digest: String,
    @SerialName("sender") val sender: SuiGraphQlAddressNodeDto? = null,
    @SerialName("effects") val effects: SuiTransactionEffectsGraphQlDto? = null
)

@Serializable
data class SuiGraphQlAddressNodeDto(
    @SerialName("address") val address: String? = null
)

@Serializable
data class SuiTransactionEffectsGraphQlDto(
    @SerialName("status") val status: String? = null,
    @SerialName("timestamp") val timestamp: String? = null,
    @SerialName("checkpoint") val checkpoint: SuiCheckpointSummaryGraphQlDto? = null,
    @SerialName("executionError") val executionError: SuiExecutionErrorGraphQlDto? = null,
    @SerialName("balanceChanges") val balanceChanges: SuiBalanceChangesConnectionGraphQlDto? = null
)

@Serializable
data class SuiCheckpointSummaryGraphQlDto(
    @SerialName("sequenceNumber") val sequenceNumber: Long? = null
)

@Serializable
data class SuiExecutionErrorGraphQlDto(
    @SerialName("message") val message: String? = null
)

@Serializable
data class SuiBalanceChangesConnectionGraphQlDto(
    @SerialName("nodes") val nodes: List<SuiBalanceChangeNodeGraphQlDto> = emptyList()
)

@Serializable
data class SuiBalanceChangeNodeGraphQlDto(
    @SerialName("amount") val amount: String? = null,
    @SerialName("owner") val owner: SuiGraphQlAddressNodeDto? = null,
    @SerialName("coinType") val coinType: SuiMoveTypeGraphQlDto? = null
)
