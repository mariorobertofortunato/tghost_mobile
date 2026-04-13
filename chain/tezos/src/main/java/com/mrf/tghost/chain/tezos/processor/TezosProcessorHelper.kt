package com.mrf.tghost.chain.tezos.processor

import com.mrf.tghost.domain.model.SupportedChain
import com.mrf.tghost.domain.model.Token
import com.mrf.tghost.domain.model.TokenAccount
import com.mrf.tghost.domain.model.TokenAccountCategories
import com.mrf.tghost.domain.model.TokenMarketData
import com.mrf.tghost.chain.tezos.domain.model.TezosToken
import com.mrf.tghost.chain.tezos.domain.model.TezosTokenMetadata
import com.mrf.tghost.chain.tezos.utils.TEZOS_NATIVE_XTZ_ACCOUNT
import kotlin.math.pow

object TezosProcessorHelper {

    // Native XTZ item
    fun createTezosNativeTokenAccountItem(
        tezosPriceUSd: Double,
        tezosAmount: Double
    ): TokenAccount {
        val finalAmount = tezosAmount / 1_000_000.0 // Tezos has 6 decimals
        val priceUsd = tezosPriceUSd.toString()

        return TokenAccount(
            pubkey = TEZOS_NATIVE_XTZ_ACCOUNT,
            name = "Tezos",
            symbol = "XTZ",
            uri = "https://tzkt.io/XTZ",
            amount = tezosAmount.toString(),
            decimals = 6,
            amountDouble = finalAmount,
            uiAmountString = finalAmount.toString(),
            description = "Tezos",
            chainId = SupportedChain.TEZ.chain.name,
            priceUsd = priceUsd,
            valueUsd = finalAmount * tezosPriceUSd,
            valueNative = finalAmount,
            info = TokenMarketData(
                imageUrl = "https://tzkt.io/logo.png",
                header = "Tezos"
            ),
            baseToken = Token(address = "XTZ", name = "Tezos", symbol = "XTZ"),
            image = null,
            tokenAccountCategory = TokenAccountCategories.HOLDINGS
        )
    }

    // Stacked TEZ item
    fun createStakedTezAccountItem(
        bakerAlias: String,
        bakerAddress: String,
        stakedTezAmount: Double,
        tezPriceUsd: Double
    ): TokenAccount {
        val totalStakedMutez = (stakedTezAmount * 1_000_000).toLong()
        val stakedValueUsd = stakedTezAmount * tezPriceUsd

        return TokenAccount(
            pubkey = bakerAddress,
            name = "$bakerAlias Baking",
            symbol = "XTZ",
            amount = totalStakedMutez.toString(),
            decimals = 6,
            amountDouble = stakedTezAmount,
            uiAmountString = stakedTezAmount.toString(),
            priceUsd = tezPriceUsd.toString(),
            valueUsd = stakedValueUsd,
            valueNative = stakedTezAmount,
            chainId = "tezos",
            pairAddress = "",
            uri = "",
            description = "Native Tezos Stake",
            labels = listOf("Stake"),
            fdv = 0.0,
            marketCap = 0.0,
            pairCreatedAt = 0L,
            image = null,
            tokenAccountCategory = TokenAccountCategories.DEFI
        )
    }

    // All others token items in the Tezos wallet
    fun createTezosTokenAccountItem(
        tokenAccountDetails: TezosToken
    ): TokenAccount {
        val balance = tokenAccountDetails.balance
        val metadata = tokenAccountDetails.token?.metadata
        val decimalsStr = metadata?.decimals ?: "0"
        val decimals = decimalsStr.toIntOrNull() ?: 0
        val amount = balance.toDoubleOrNull() ?: 0.0
        val uiAmount = if (decimals > 0) amount / 10.0.pow(decimals.toDouble()) else amount
        val tokenId = tokenAccountDetails.token?.tokenId
        val contractAddress = tokenAccountDetails.token?.contract?.address
        val symbol = metadata?.symbol ?: "?"
        val name = metadata?.name ?: "Unknown"
        val imageUrl = metadata?.thumbnailUri ?: metadata?.displayUri ?: metadata?.artifactUri

        return TokenAccount(
            pubkey = if (tokenId != null) "$contractAddress:$tokenId" else contractAddress,
            name = name,
            symbol = symbol,
            uri = "",
            amount = balance,
            decimals = decimals,
            amountDouble = uiAmount,
            uiAmountString = uiAmount.toString(),
            image = imageUrl,
            description = metadata?.description,
            createdOn = null,
            chainId = "tezos",
            labels = listOf(),
            baseToken = null,
            quoteToken = null,
            priceNative = "0",
            priceUsd = "0",
            pairAddress = "null",
            txns = null,
            volume = null,
            priceChange = null,
            liquidity = null,
            info = null,
            fdv = 0.0,
            marketCap = 0.0,
            pairCreatedAt = 0L,
            tokenAccountCategory = categorizeTezosToken(tokenAccountDetails),
        )
    }

    /**
     * TZKT exposes [TezosTokenInfo.standard] (`fa1.2` / `fa2`). FA1.2 is always fungible.
     * FA2 mixes fungibles (often `decimals` > 0, `tokenId` "0") and NFTs / collectibles.
     * This is heuristic; ambiguous rows default to [TokenAccountCategories.HOLDINGS].
     */
    fun categorizeTezosToken(token: TezosToken): TokenAccountCategories {
        val info = token.token ?: return TokenAccountCategories.HOLDINGS
        if (isFa12Standard(info.standard)) return TokenAccountCategories.HOLDINGS

        val metadata = info.metadata
        val decimals = metadata?.decimals?.toIntOrNull() ?: 0
        if (decimals > 0) return TokenAccountCategories.HOLDINGS

        val tokenId = info.tokenId
        if (tokenId != null && tokenId != "0") return TokenAccountCategories.NFTS

        if (hasTzip21Media(metadata)) return TokenAccountCategories.NFTS

        return TokenAccountCategories.HOLDINGS
    }

    private fun isFa12Standard(standard: String?): Boolean {
        return when (standard?.lowercase()?.trim()) {
            "fa1.2", "fa1_2", "fa12", "fa1" -> true
            else -> false
        }
    }

    private fun hasTzip21Media(metadata: TezosTokenMetadata?): Boolean {
        if (metadata == null) return false
        return !metadata.displayUri.isNullOrBlank() ||
                !metadata.thumbnailUri.isNullOrBlank() ||
                !metadata.artifactUri.isNullOrBlank()
    }
}