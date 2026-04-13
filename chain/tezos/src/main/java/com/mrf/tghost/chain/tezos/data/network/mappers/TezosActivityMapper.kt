package com.mrf.tghost.chain.tezos.data.network.mappers

import com.mrf.tghost.chain.tezos.data.network.model.TezosActivityDto
import com.mrf.tghost.domain.model.BalanceChange
import com.mrf.tghost.domain.model.Transaction
import com.mrf.tghost.domain.model.TransactionType
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import java.time.Instant

private const val CHAIN_TEZOS = "tezos"
private const val XTZ_SYMBOL = "XTZ"
private const val XTZ_DECIMALS = 6

private const val TYPE_TRANSACTION = "transaction"
private const val TYPE_DELEGATION = "delegation"
private const val TYPE_TOKEN_TRANSFER = "token_transfer"

fun TezosActivityDto.toDomainModel(accountAddress: String): Transaction {
    val idStr = hash ?: "${type}_$id"
    val tsSeconds = timestamp?.let { runCatching { Instant.parse(it).epochSecond }.getOrNull() }
    val errStr = errors?.takeIf { it.isNotEmpty() }?.joinToString(separator = "; ") { it.toString() }
    val success = isSuccess()

    return when (type) {
        TYPE_TRANSACTION -> toTransactionOperation(idStr, accountAddress, tsSeconds, success, errStr)
        TYPE_DELEGATION -> toDelegationOperation(idStr, accountAddress, tsSeconds, success, errStr)
        TYPE_TOKEN_TRANSFER -> toTokenTransfer(idStr, accountAddress, tsSeconds, errStr)
        else -> Transaction(
            id = idStr,
            chain = CHAIN_TEZOS,
            blockNumber = level?.toLong(),
            timestamp = tsSeconds,
            fee = null,
            isSuccess = success,
            error = errStr,
            balanceChanges = emptyList(),
            type = TransactionType.UNKNOWN,
        )
    }
}

private fun TezosActivityDto.isSuccess(): Boolean = when {
    status != null -> status.equals("applied", ignoreCase = true)
    errors != null -> errors.isEmpty()
    else -> true
}

private fun TezosActivityDto.toTransactionOperation(
    idStr: String,
    accountAddress: String,
    tsSeconds: Long?,
    success: Boolean,
    errStr: String?,
): Transaction {
    val fees = (bakerFee ?: 0L) + (storageFee ?: 0L) + (allocationFee ?: 0L)
    val mutezAmount = amount.toLongAmount() ?: 0L
    val balanceChanges = mutableListOf<BalanceChange>()

    val isSender = sender?.address == accountAddress
    val isTarget = target?.address == accountAddress

    when {
        isSender && isTarget -> balanceChanges.add(
            BalanceChange(accountAddress, -fees, XTZ_SYMBOL, XTZ_DECIMALS, isNative = true),
        )
        isSender -> {
            balanceChanges.add(
                BalanceChange(accountAddress, -(mutezAmount + fees), XTZ_SYMBOL, XTZ_DECIMALS, isNative = true),
            )
        }
        isTarget -> {
            balanceChanges.add(
                BalanceChange(accountAddress, mutezAmount, XTZ_SYMBOL, XTZ_DECIMALS, isNative = true),
            )
        }
    }

    val txType = resolveTransactionType(mutezAmount, isTarget)

    return Transaction(
        id = idStr,
        chain = CHAIN_TEZOS,
        blockNumber = level?.toLong(),
        timestamp = tsSeconds,
        fee = fees.takeIf { it > 0L },
        isSuccess = success,
        error = errStr,
        balanceChanges = balanceChanges,
        type = txType,
    )
}

private fun TezosActivityDto.resolveTransactionType(mutezAmount: Long, isTarget: Boolean): TransactionType {
    val entrypoint = parameter?.let { extractEntrypoint(it) }
    return when {
        entrypoint != null && entrypoint != "default" && entrypoint.isNotEmpty() ->
            TransactionType.CONTRACT_INTERACTION
        mutezAmount > 0L || isTarget -> TransactionType.TRANSFER
        parameter != null -> TransactionType.CONTRACT_INTERACTION
        else -> TransactionType.UNKNOWN
    }
}

private fun TezosActivityDto.toDelegationOperation(
    idStr: String,
    accountAddress: String,
    tsSeconds: Long?,
    success: Boolean,
    errStr: String?,
): Transaction {
    val fees = bakerFee ?: 0L
    val balanceChanges = mutableListOf<BalanceChange>()
    if (sender?.address == accountAddress && fees > 0L) {
        balanceChanges.add(
            BalanceChange(accountAddress, -fees, XTZ_SYMBOL, XTZ_DECIMALS, isNative = true),
        )
    }
    val delegationType = if (newDelegate == null) TransactionType.UNSTAKE else TransactionType.STAKE

    return Transaction(
        id = idStr,
        chain = CHAIN_TEZOS,
        blockNumber = level?.toLong(),
        timestamp = tsSeconds,
        fee = fees.takeIf { it > 0L },
        isSuccess = success,
        error = errStr,
        balanceChanges = balanceChanges,
        type = delegationType,
    )
}

private fun TezosActivityDto.toTokenTransfer(
    idStr: String,
    accountAddress: String,
    tsSeconds: Long?,
    errStr: String?,
): Transaction {
    val rawAmount = amount.toLongAmount() ?: 0L
    val decimals = token?.metadata?.let { readMetadataInt(it, "decimals") } ?: 0
    val symbol = token?.metadata?.let { readMetadataString(it, "symbol") } ?: "TOKEN"
    val mint = token?.contract?.address

    val balanceChanges = mutableListOf<BalanceChange>()
    when {
        from?.address == accountAddress -> balanceChanges.add(
            BalanceChange(
                address = accountAddress,
                amount = -rawAmount,
                symbol = symbol,
                decimals = decimals,
                isNative = false,
                mint = mint,
            ),
        )
        to?.address == accountAddress -> balanceChanges.add(
            BalanceChange(
                address = accountAddress,
                amount = rawAmount,
                symbol = symbol,
                decimals = decimals,
                isNative = false,
                mint = mint,
            ),
        )
    }

    return Transaction(
        id = idStr,
        chain = CHAIN_TEZOS,
        blockNumber = level?.toLong(),
        timestamp = tsSeconds,
        fee = null,
        isSuccess = errStr == null,
        error = errStr,
        balanceChanges = balanceChanges,
        type = TransactionType.TRANSFER,
    )
}

private fun extractEntrypoint(parameter: JsonElement): String? =
    (parameter as? JsonObject)?.get("entrypoint")?.jsonPrimitive?.contentOrNull

private fun readMetadataString(metadata: JsonElement, key: String): String? {
    val obj = metadata as? JsonObject ?: return null
    val v = obj[key] ?: return null
    val prim = v as? JsonPrimitive ?: return null
    return prim.contentOrNull?.takeIf { it.isNotEmpty() }
}

private fun readMetadataInt(metadata: JsonElement, key: String): Int? {
    val obj = metadata as? JsonObject ?: return null
    val v = obj[key] ?: return null
    val prim = v as? JsonPrimitive ?: return null
    prim.intOrNull?.let { return it }
    if (prim.isString) return prim.content.toIntOrNull()
    return null
}

private fun JsonElement?.toLongAmount(): Long? {
    val prim = this as? JsonPrimitive ?: return null
    prim.longOrNull?.let { return it }
    if (prim.isString) return prim.content.toLongOrNull()
    return null
}
