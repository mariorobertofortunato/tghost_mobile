package com.mrf.tghost.chain.evm.data.network.mappers

import com.mrf.tghost.chain.evm.data.network.model.moralis.MoralisActivityErc20TransferDto
import com.mrf.tghost.chain.evm.data.network.model.moralis.MoralisActivityItemDto
import com.mrf.tghost.chain.evm.data.network.model.moralis.MoralisActivityNativeTransferDto
import com.mrf.tghost.chain.evm.data.network.model.moralis.MoralisActivityResponseDto
import com.mrf.tghost.domain.model.BalanceChange
import com.mrf.tghost.domain.model.EvmChain
import com.mrf.tghost.domain.model.Transaction
import com.mrf.tghost.domain.model.TransactionType
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.longOrNull
import java.math.BigInteger
import java.time.Instant

private const val DEFAULT_ERC20_SYMBOL = "TOKEN"
private const val DEFAULT_NATIVE_SYMBOL = "ETH"
private const val DEFAULT_NATIVE_DECIMALS = 18

fun MoralisActivityResponseDto.toDomainModel(accountAddress: String, chainId: EvmChain): List<Transaction> =
    result.map { it.toDomainTransaction(accountAddress, chainId) }

private fun MoralisActivityItemDto.toDomainTransaction(accountAddress: String, chainId: EvmChain): Transaction {
    val normalizedAccount = accountAddress.lowercase()
    val changes = buildList {
        erc20Transfers.forEach { transfer ->
            transfer.toBalanceChangeOrNull(normalizedAccount)?.let(::add)
        }
        nativeTransfers.forEach { transfer ->
            transfer.toBalanceChangeOrNull(normalizedAccount)?.let(::add)
        }
    }

    return Transaction(
        id = hash,
        chain = chainId.chain,
        blockNumber = blockNumber?.toLongOrNull(),
        timestamp = blockTimestamp.toEpochSecondsOrNull(),
        fee = gasPrice.toBigIntegerOrNull()
            ?.multiply(receiptGasUsed.toBigIntegerOrNull() ?: BigInteger.ZERO)
            .toLongClampedOrNull(),
        isSuccess = receiptStatus == null || receiptStatus == "1",
        error = null,
        balanceChanges = changes,
        type = inferTransactionType(changes),
    )
}

private fun MoralisActivityErc20TransferDto.toBalanceChangeOrNull(normalizedAccountAddress: String): BalanceChange? {
    val from = fromAddress?.lowercase()
    val to = toAddress?.lowercase()
    val rawAmount = value.toLongAmountLenient() ?: return null
    if (rawAmount == 0L) return null
    val signedAmount = when {
        from == normalizedAccountAddress && to == normalizedAccountAddress -> 0L
        from == normalizedAccountAddress -> -rawAmount
        to == normalizedAccountAddress -> rawAmount
        else -> return null
    }
    if (signedAmount == 0L) return null

    return BalanceChange(
        address = normalizedAccountAddress,
        amount = signedAmount,
        symbol = tokenSymbol?.takeIf { it.isNotBlank() } ?: DEFAULT_ERC20_SYMBOL,
        decimals = tokenDecimals?.toIntOrNull() ?: 0,
        isNative = false,
        mint = tokenAddress,
    )
}

private fun MoralisActivityNativeTransferDto.toBalanceChangeOrNull(normalizedAccountAddress: String): BalanceChange? {
    val from = fromAddress?.lowercase()
    val to = toAddress?.lowercase()
    val rawAmount = value?.toLongOrNull() ?: return null
    if (rawAmount == 0L) return null
    val signedAmount = when {
        from == normalizedAccountAddress && to == normalizedAccountAddress -> 0L
        from == normalizedAccountAddress -> -rawAmount
        to == normalizedAccountAddress -> rawAmount
        else -> return null
    }
    if (signedAmount == 0L) return null

    return BalanceChange(
        address = normalizedAccountAddress,
        amount = signedAmount,
        symbol = tokenSymbol?.takeIf { it.isNotBlank() } ?: DEFAULT_NATIVE_SYMBOL,
        decimals = DEFAULT_NATIVE_DECIMALS,
        isNative = true,
    )
}

private fun inferTransactionType(balanceChanges: List<BalanceChange>): TransactionType {
    if (balanceChanges.isEmpty()) return TransactionType.UNKNOWN
    return when {
        balanceChanges.any { !it.isNative } -> TransactionType.TRANSFER
        balanceChanges.size <= 3 -> TransactionType.TRANSFER
        else -> TransactionType.UNKNOWN
    }
}

private fun String?.toEpochSecondsOrNull(): Long? =
    this?.let { runCatching { Instant.parse(it).epochSecond }.getOrNull() }

private fun String?.toBigIntegerOrNull(): BigInteger? {
    val value = this?.trim().orEmpty()
    if (value.isEmpty()) return null
    return runCatching { BigInteger(value) }.getOrNull()
}

private fun BigInteger?.toLongClampedOrNull(): Long? {
    val value = this ?: return null
    return when {
        value > BigInteger.valueOf(Long.MAX_VALUE) -> Long.MAX_VALUE
        value < BigInteger.valueOf(Long.MIN_VALUE) -> Long.MIN_VALUE
        else -> value.toLong()
    }
}

private fun kotlinx.serialization.json.JsonElement?.toLongAmountLenient(): Long? {
    val primitive = this as? JsonPrimitive ?: return null
    primitive.longOrNull?.let { return it }
    if (primitive.isString) return primitive.content.toLongOrNull()
    return null
}
