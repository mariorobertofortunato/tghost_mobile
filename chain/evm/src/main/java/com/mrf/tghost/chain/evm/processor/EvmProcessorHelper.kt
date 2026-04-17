package com.mrf.tghost.chain.evm.processor

import com.mrf.tghost.chain.evm.domain.model.EvmNftResult
import com.mrf.tghost.chain.evm.domain.model.EvmStakingProtocol
import com.mrf.tghost.chain.evm.domain.model.EvmTokenAccount
import com.mrf.tghost.chain.evm.utils.ARB_ETH_TOKEN_MINT
import com.mrf.tghost.chain.evm.utils.ETH_TOKEN_MINT
import com.mrf.tghost.chain.evm.utils.L2_ETH_TOKEN_MINT
import com.mrf.tghost.chain.evm.utils.LINEA_ETH_TOKEN_MINT
import com.mrf.tghost.domain.model.TokenAccount
import com.mrf.tghost.domain.model.TokenAccountCategories
import com.mrf.tghost.domain.model.TokenHelpers
import com.mrf.tghost.domain.model.TokenMarketData
import com.mrf.tghost.domain.model.TokenMarketDataInfo
import java.math.BigDecimal

object EvmProcessorHelper {

    // Native (liquid) ETH item
    fun createEthNativeTokenAccountItem(
        address: String,
        quote: TokenMarketDataInfo?,
        balance: Long
    ): TokenAccount {
        return TokenAccount(
            pubkey = address,
            name = "Ethereum",
            symbol = quote?.baseToken?.symbol ?: "ETH",
            uri = quote?.url ?: "",
            decimals = 18,
            amountDouble = balance / 1_000_000_000_000_000_000.0, //Conversion
            image = null,
            description = "Ethereum",
            createdOn = null,
            chainId = quote?.chainId ?: "ethereum",
            pairAddress = quote?.pairAddress ?: "",
            labels = emptyList(),
            priceUsd = quote?.priceUsd ?: "1",
            fdv = quote?.fdv ?: 0.0,
            marketCap = quote?.marketCap ?: 0.0,
            pairCreatedAt = quote?.pairCreatedAt ?: 0L,
            info = quote?.info,
            baseToken = quote?.baseToken,
            quoteToken = quote?.quoteToken,
            txns = quote?.txns,
            volume = quote?.volume,
            priceChange = quote?.priceChange,
            liquidity = quote?.liquidity,
            tokenAccountCategory = TokenAccountCategories.HOLDINGS
        )
    }

    // Stacked ETH item
    fun createStakedEthAccountItem(
        stakeAccount: EvmStakingProtocol,
        stakedEthAmount: BigDecimal
    ): TokenAccount {
        val stakedAmount = stakedEthAmount.divide(BigDecimal("1000000000000000000")).toDouble()

        return TokenAccount(
            pubkey = "",
            name = "Staked ETH",
            symbol = "stETH",
            decimals = 18,
            amountDouble = stakedAmount,
            priceUsd = stakeAccount.position?.tokens?.firstOrNull()?.usdPrice.toString(),
            priceNative = "1",
            chainId = "ethereum", // This is usually Ethereum mainnet for staked ETH, unless bridged
            pairAddress = "",
            uri = "",
            image = null,
            description = "Native Ethereum Stake",
            createdOn = null,
            labels = listOf("Stake"),
            baseToken = null,
            quoteToken = null,
            txns = null,
            volume = null,
            priceChange = null,
            liquidity = null,
            fdv = 0.0,
            marketCap = 0.0,
            pairCreatedAt = 0L,
            info = null,
            tokenAccountCategory = TokenAccountCategories.DEFI
        )
    }

    // All others token items in the Evm wallet
    fun createEvmTokenAccountItem(
        tokenAccountDetails: EvmTokenAccount,
        marketDataInfo: TokenMarketDataInfo?,
        evmChainId: String?
    ): TokenAccount {
        val tokenChainId = when (tokenAccountDetails.network) {
            "eth-mainnet" -> "eth"
            "base-mainnet" -> "base"
            else -> evmChainId
        }
        val decimals = tokenAccountDetails.decimals ?: 18
        val amountBigInt = tokenAccountDetails.balance
        val uiAmount = amountBigInt.movePointLeft(decimals).toDouble()

        val alchemyUsdPrice = tokenAccountDetails.prices
            .firstOrNull { it.currency?.equals("usd", ignoreCase = true) == true }
            ?.value

        val priceUsd = when {
            tokenAccountDetails.isNative ->
                alchemyUsdPrice?.takeIf { it.isNotBlank() }
                    ?: marketDataInfo?.priceUsd
                    ?: "0"
            else -> marketDataInfo?.priceUsd ?: "0"
        }

        val priceNative = if (tokenAccountDetails.isNative) {
            "1"
        } else {
            marketDataInfo?.priceNative ?: "0"
        }

        return TokenAccount(
            pubkey = tokenAccountDetails.contractAddress,
            name = tokenAccountDetails.name ?: marketDataInfo?.baseToken?.name ?:  "Unknown",
            symbol = tokenAccountDetails.symbol ?: marketDataInfo?.baseToken?.symbol ?:  "?",
            uri = marketDataInfo?.url ?: "",
            decimals = decimals,
            amountDouble = uiAmount,
            image = tokenAccountDetails.logo ?: marketDataInfo?.info?.imageUrl,
            description = null,
            createdOn = null,
            chainId = tokenChainId,
            pairAddress = marketDataInfo?.pairAddress ?: "",
            labels = marketDataInfo?.labels ?: emptyList(),
            baseToken = marketDataInfo?.baseToken,
            quoteToken = if (tokenAccountDetails.isNative) null else marketDataInfo?.quoteToken,
            priceNative = priceNative,
            priceUsd = priceUsd,
            txns = marketDataInfo?.txns,
            volume = marketDataInfo?.volume,
            priceChange = marketDataInfo?.priceChange,
            liquidity = marketDataInfo?.liquidity,
            fdv = marketDataInfo?.fdv ?: 0.0,
            marketCap = marketDataInfo?.marketCap ?: 0.0,
            pairCreatedAt = marketDataInfo?.pairCreatedAt ?: 0,
            info = marketDataInfo?.info ?: TokenMarketData(),
            tokenAccountCategory = TokenHelpers.getTokenAccountCategory(
                marketDataInfo?.baseToken?.name ?: tokenAccountDetails.name ?: TokenAccountCategories.HOLDINGS.name,
            )
        )
    }

    // NFTs
    fun createEvmNftAccountItem(
        nftResult: EvmNftResult,
        evmChainId: String?
    ): TokenAccount {
        val nftKey = listOfNotNull(
            evmChainId,
            nftResult.tokenAddress?.lowercase(),
            nftResult.tokenId,
        ).joinToString(":")

        return TokenAccount(
            name = nftResult.name,
            symbol = nftResult.symbol,
            decimals = 18,
            amountDouble = nftResult.amount?.toDouble(),
            priceUsd = nftResult.listPrice?.priceUsd ?: "0",
            priceNative = "0",
            chainId = evmChainId,
            pubkey = nftKey,
            pairAddress = "",
            uri = "",
            image = nftResult.normalizedMetadata?.image ?: "",
            description = nftResult.normalizedMetadata?.description ?: "",
            createdOn = null,
            labels = listOf("Stake"),
            baseToken = null,
            quoteToken = null,
            txns = null,
            volume = null,
            priceChange = null,
            liquidity = null,
            fdv = 0.0,
            marketCap = 0.0,
            pairCreatedAt = 0L,
            info = null,
            tokenAccountCategory = TokenAccountCategories.NFTS
        )
    }

    fun nativeMintFor(chain: String): String =
        when (chain.lowercase()) {
            "eth", "ethereum" -> ETH_TOKEN_MINT
            "optimism", "base", "zora", "mode" -> L2_ETH_TOKEN_MINT
            "arbitrum" -> ARB_ETH_TOKEN_MINT
            "linea" -> LINEA_ETH_TOKEN_MINT
            else -> error("Unsupported chain: $chain")
        }

    fun isValidNft(nft: EvmNftResult): Boolean {
        val spam = nft.possibleSpam
        if (spam == true) return false

        val name = nft.name
        val normName = nft.normalizedMetadata?.name
        if (name == null && normName == null) return false
        if (name == "" && normName == "") return false
        if (name == " " && normName == " ") return false

        val uri = nft.normalizedMetadata?.externalUrl ?: ""
        val link = nft.normalizedMetadata?.externalLink ?: ""
        if (uri.contains(".today") ||
            uri.contains("pastebin") ||
            uri.contains("onlinehostingipfs") ||
            uri.contains("ibb.co") ||
            link.contains(".today") ||
            link.contains("pastebin") ||
            link.contains("onlinehostingipfs") ||
            link.contains("ibb.co")
        ) return false

        return true
    }

}