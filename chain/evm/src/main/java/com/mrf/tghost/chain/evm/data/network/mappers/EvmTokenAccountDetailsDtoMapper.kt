package com.mrf.tghost.chain.evm.data.network.mappers

import com.mrf.tghost.chain.evm.data.network.model.alchemy.AlchemyTokenDto
import com.mrf.tghost.chain.evm.data.network.model.moralis.MoralisWalletTokensResponseDto
import com.mrf.tghost.chain.evm.data.network.model.moralis.MoralisWalletTokenDto
import com.mrf.tghost.chain.evm.domain.model.EvmTokenAccount
import com.mrf.tghost.chain.evm.domain.model.EvmTokenPrice
import com.mrf.tghost.chain.evm.utils.ETH_TOKEN_MINT
import com.mrf.tghost.chain.evm.utils.L2_ETH_TOKEN_MINT
import com.mrf.tghost.domain.model.EvmChain
import java.math.BigDecimal
import java.math.BigInteger

private const val MORALIS_NATIVE_TOKEN_PLACEHOLDER = "0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"

fun MoralisWalletTokensResponseDto.toEvmTokenAccounts(chainId: EvmChain): List<EvmTokenAccount> =
    result.map { it.toEvmTokenAccount(chainId) }

private fun MoralisWalletTokenDto.toEvmTokenAccount(chainId: EvmChain): EvmTokenAccount {
    val isPlaceholder = tokenAddress.equals(MORALIS_NATIVE_TOKEN_PLACEHOLDER, ignoreCase = true)
    val isNative = nativeToken == true || isPlaceholder
    val contractAddress = when {
        isNative -> when (chainId) {
            EvmChain.ETHEREUM -> ETH_TOKEN_MINT
            EvmChain.BASE -> L2_ETH_TOKEN_MINT
        }
        else -> tokenAddress
    }
    val rawBalance = balance ?: "0"
    val balanceBd = rawBalance.toBigDecimalOrNull() ?: BigDecimal.ZERO
    val usdPrices = usdPrice?.takeIf { it > 0.0 }?.let { price ->
        listOf(EvmTokenPrice(currency = "usd", value = price.toString()))
    }.orEmpty()
    return EvmTokenAccount(
        contractAddress = contractAddress,
        name = name,
        symbol = symbol,
        decimals = decimals,
        balance = balanceBd,
        rawBalance = rawBalance,
        logo = logo ?: thumbnail,
        prices = usdPrices,
        isNative = isNative,
    )
}

/*fun List<EvmTokenAccountDetailsDto>.toDomainModel(): List<EvmTokenAccount> {
    return this.map { it ->
        it.toDomainModel()
    }
}


fun EvmTokenAccountDetailsDto.toDomainModel(): EvmTokenAccount {
    return EvmTokenAccount(
        contractAddress = contractAddress,
        name = name,
        symbol = symbol,
        decimals = decimals,
        balance = balance?.toBigDecimal() ?: BigDecimal.ZERO,
        rawBalance = balance ?: "0"
    )
}*/

fun List<AlchemyTokenDto>.toDomainModelFromAlchemy(): List<EvmTokenAccount> {
    return this.map { token ->
        val isNative = token.tokenAddress.isNullOrBlank()
        val contractAddress = token.tokenAddress
            ?.takeIf { it.isNotBlank() }
            ?: token.network.toNativeContractAddress()
        val rawBalance = token.tokenBalance ?: "0"
        EvmTokenAccount(
            contractAddress = contractAddress,
            name = token.tokenMetadata?.name ?: token.network.toNativeName(),
            symbol = token.tokenMetadata?.symbol ?: token.network.toNativeSymbol(),
            decimals = token.tokenMetadata?.decimals,
            balance = rawBalance.toRawBalanceBigDecimal(),
            rawBalance = rawBalance,
            ownerAddress = token.ownerAddress,
            network = token.network,
            logo = token.tokenMetadata?.logo,
            prices = token.tokenPrices.map { price ->
                EvmTokenPrice(
                    currency = price.currency,
                    value = price.value,
                    lastUpdatedAt = price.lastUpdatedAt,
                )
            },
            isNative = isNative,
        )
    }
}

private fun String?.toNativeContractAddress(): String = when (this) {
    "eth-mainnet" -> ETH_TOKEN_MINT
    "base-mainnet" -> L2_ETH_TOKEN_MINT
    else -> ""
}

private fun String?.toNativeSymbol(): String? = when (this) {
    "eth-mainnet", "base-mainnet" -> "ETH"
    else -> null
}

private fun String?.toNativeName(): String? = when (this) {
    "eth-mainnet", "base-mainnet" -> "Ethereum"
    else -> null
}

private fun String.toRawBalanceBigDecimal(): BigDecimal {
    val value = trim()
    if (value.isEmpty()) return BigDecimal.ZERO

    return try {
        if (value.startsWith("0x", ignoreCase = true)) {
            BigInteger(value.removePrefix("0x"), 16).toBigDecimal()
        } else {
            value.toBigDecimal()
        }
    } catch (_: Exception) {
        BigDecimal.ZERO
    }
}
