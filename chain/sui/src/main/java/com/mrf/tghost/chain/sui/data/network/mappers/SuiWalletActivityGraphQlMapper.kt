package com.mrf.tghost.chain.sui.data.network.mappers

import com.mrf.tghost.chain.sui.data.network.model.SuiBalanceChangeNodeGraphQlDto
import com.mrf.tghost.chain.sui.data.network.model.SuiTransactionNodeGraphQlDto
import com.mrf.tghost.domain.model.BalanceChange
import com.mrf.tghost.domain.model.Transaction
import com.mrf.tghost.domain.model.TransactionType
import java.math.BigInteger
import java.time.Instant

private const val SUI_CHAIN_LABEL = "sui"
private const val SUI_NATIVE_MARKER = "::sui::SUI"
private const val SUI_DECIMALS = 9
private const val SUI_SYMBOL = "SUI"

fun List<SuiTransactionNodeGraphQlDto>.toDomainTransactions(): List<Transaction> =
    mapNotNull { it.toDomainTransactionOrNull() }
        .sortedWith(
            compareByDescending<Transaction> { it.timestamp ?: Long.MIN_VALUE }
                .thenByDescending { it.blockNumber ?: Long.MIN_VALUE }
        )

private fun SuiTransactionNodeGraphQlDto.toDomainTransactionOrNull(): Transaction? {
    val eff = effects ?: return null
    val balanceChanges = eff.balanceChanges?.nodes.orEmpty().mapNotNull { it.toBalanceChangeOrNull() }
    val timestampSec = eff.timestamp?.let { ts ->
        runCatching { Instant.parse(ts).epochSecond }.getOrNull()
    }
    val success = eff.status?.equals("SUCCESS", ignoreCase = true) == true

    return Transaction(
        id = digest,
        chain = SUI_CHAIN_LABEL,
        blockNumber = eff.checkpoint?.sequenceNumber,
        timestamp = timestampSec,
        fee = null,
        isSuccess = success,
        error = eff.executionError?.message,
        balanceChanges = balanceChanges,
        type = inferTransactionType(balanceChanges)
    )
}

private fun SuiBalanceChangeNodeGraphQlDto.toBalanceChangeOrNull(): BalanceChange? {
    val repr = coinType?.repr ?: return null
    val ownerAddr = owner?.address ?: return null
    val rawAmount = amount.toLongAmountClamped()
    if (rawAmount == 0L) return null
    val native = repr.contains(SUI_NATIVE_MARKER, ignoreCase = true)
    return BalanceChange(
        address = ownerAddr,
        amount = rawAmount,
        symbol = if (native) SUI_SYMBOL else repr.substringAfterLast("::", "TOKEN"),
        decimals = SUI_DECIMALS,
        isNative = native,
        mint = if (native) null else repr
    )
}

private fun String?.toLongAmountClamped(): Long {
    val s = this?.trim()?.takeIf { it.isNotEmpty() } ?: return 0L
    val bi = try {
        BigInteger(s)
    } catch (_: NumberFormatException) {
        return 0L
    }
    return when {
        bi > BigInteger.valueOf(Long.MAX_VALUE) -> Long.MAX_VALUE
        bi < BigInteger.valueOf(Long.MIN_VALUE) -> Long.MIN_VALUE
        else -> bi.toLong()
    }
}

private fun inferTransactionType(changes: List<BalanceChange>): TransactionType {
    if (changes.isEmpty()) return TransactionType.UNKNOWN
    val distinctMints = changes.mapNotNull { it.mint }.distinct()
    val hasNonNative = changes.any { !it.isNative }
    return when {
        distinctMints.size > 1 || hasNonNative -> TransactionType.TRANSFER
        changes.size <= 3 -> TransactionType.TRANSFER
        else -> TransactionType.UNKNOWN
    }
}
