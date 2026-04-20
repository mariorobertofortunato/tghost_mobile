package com.mrf.tghost.chain.evm.data.network.mappers

import com.mrf.tghost.chain.evm.data.network.model.alchemy.AlchemyAssetTransferDto
import com.mrf.tghost.domain.model.BalanceChange
import com.mrf.tghost.domain.model.EvmChain
import com.mrf.tghost.domain.model.Transaction
import com.mrf.tghost.domain.model.TransactionType
import java.math.BigInteger
import java.time.Instant

private const val DEFAULT_ERC20_SYMBOL = "TOKEN"
private const val DEFAULT_NATIVE_SYMBOL = "ETH"
private const val DEFAULT_NATIVE_DECIMALS = 18

fun List<AlchemyAssetTransferDto>.toDomainTransactionsFromAlchemy(
    accountAddress: String,
    chainId: EvmChain,
): List<Transaction> {
    val normalizedAddress = accountAddress.lowercase()
    return mapNotNull { it.toDomainTransactionOrNull(normalizedAddress, chainId) }
}

private fun AlchemyAssetTransferDto.toDomainTransactionOrNull(
    normalizedAccountAddress: String,
    chainId: EvmChain,
): Transaction? {
    val hashValue = hash ?: uniqueId ?: return null
    val amountAbs = rawContract.toRawAmountOrNull() ?: return null
    if (amountAbs == 0L) return null

    val fromAddress = from?.lowercase()
    val toAddress = to?.lowercase()
    val signedAmount = when {
        fromAddress == normalizedAccountAddress && toAddress == normalizedAccountAddress -> 0L
        fromAddress == normalizedAccountAddress -> -amountAbs
        toAddress == normalizedAccountAddress -> amountAbs
        else -> return null
    }
    if (signedAmount == 0L) return null

    val mint = rawContract?.address
    val isNative = mint.isNullOrBlank()
    val decimals = if (isNative) {
        DEFAULT_NATIVE_DECIMALS
    } else {
        rawContract?.decimal.toIntFromHexOrNull() ?: 0
    }
    val symbol = asset?.takeIf { it.isNotBlank() }
        ?: if (isNative) DEFAULT_NATIVE_SYMBOL else DEFAULT_ERC20_SYMBOL

    return Transaction(
        id = hashValue,
        chain = chainId.chain,
        blockNumber = blockNum.toLongFromHexOrNull(),
        timestamp = metadata?.blockTimestamp.toEpochSecondsOrNull(),
        fee = null,
        isSuccess = true,
        error = null,
        balanceChanges = listOf(
            BalanceChange(
                address = normalizedAccountAddress,
                amount = signedAmount,
                symbol = symbol,
                decimals = decimals,
                isNative = isNative,
                mint = mint,
            )
        ),
        type = TransactionType.TRANSFER,
    )
}

private fun String?.toEpochSecondsOrNull(): Long? =
    this?.let { runCatching { Instant.parse(it).epochSecond }.getOrNull() }

private fun String?.toLongFromHexOrNull(): Long? {
    val value = this?.trim().orEmpty()
    if (value.isEmpty()) return null
    return runCatching {
        val asBigInt = if (value.startsWith("0x", ignoreCase = true)) {
            BigInteger(value.removePrefix("0x"), 16)
        } else {
            BigInteger(value)
        }
        when {
            asBigInt > BigInteger.valueOf(Long.MAX_VALUE) -> Long.MAX_VALUE
            asBigInt < BigInteger.valueOf(Long.MIN_VALUE) -> Long.MIN_VALUE
            else -> asBigInt.toLong()
        }
    }.getOrNull()
}

private fun String?.toIntFromHexOrNull(): Int? {
    val value = this?.trim().orEmpty()
    if (value.isEmpty()) return null
    return runCatching {
        if (value.startsWith("0x", ignoreCase = true)) {
            value.removePrefix("0x").toInt(16)
        } else {
            value.toInt()
        }
    }.getOrNull()
}

private fun com.mrf.tghost.chain.evm.data.network.model.alchemy.AlchemyRawContractDto?.toRawAmountOrNull(): Long? {
    val rawHex = this?.value?.trim().orEmpty()
    if (rawHex.isNotEmpty()) {
        return runCatching {
            val asBigInt = if (rawHex.startsWith("0x", ignoreCase = true)) {
                BigInteger(rawHex.removePrefix("0x"), 16)
            } else {
                BigInteger(rawHex)
            }
            when {
                asBigInt > BigInteger.valueOf(Long.MAX_VALUE) -> Long.MAX_VALUE
                asBigInt < BigInteger.valueOf(Long.MIN_VALUE) -> Long.MIN_VALUE
                else -> asBigInt.toLong()
            }
        }.getOrNull()
    }

    return null
}
