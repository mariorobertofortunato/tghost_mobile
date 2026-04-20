package com.mrf.tghost.chain.evm.processor

import com.mrf.tghost.chain.evm.domain.model.EvmNftResult
import com.mrf.tghost.chain.evm.domain.model.EvmStakingProtocol
import com.mrf.tghost.chain.evm.domain.model.Position
import com.mrf.tghost.chain.evm.domain.model.PositionToken
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
import java.math.RoundingMode
import kotlin.math.abs

object EvmProcessorHelper {

    /**
     * Dexscreener chain id for [getMarketDataUseCase] (native + token pairs on same chain).
     */
    fun resolveDexChainId(network: String?, evmChainFallback: String?): String =
        when (network) {
            "eth-mainnet" -> "ethereum"
            "base-mainnet" -> "base"
            else -> when (evmChainFallback) {
                "base" -> "base"
                "eth" -> "ethereum"
                else -> "ethereum"
            }
        }

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

    // Staking / DeFi position (Moralis defi positions)
    fun createStakedEthAccountItem(
        stakeAccount: EvmStakingProtocol,
        evmChainId: String?,
        ethUsdPrice: Double? = null,
    ): TokenAccount {
        val position = stakeAccount.position
        val tokens = position?.tokens.orEmpty()
        val totalUsd = resolvePositionTotalUsd(position)

        val supplyTokens = tokens.filter { !isDebtLikeToken(it) }
        val ethLikeSupply = supplyTokens.filter { isEthLikeSymbol(it.symbol) }

        val ethEquivalentAmount = ethLikeSupply.sumOf { parseStakingTokenAmount(it) }
        val primaryToken = ethLikeSupply.maxByOrNull { it.usdValue ?: 0.0 }
            ?: supplyTokens.maxByOrNull { it.usdValue ?: 0.0 }
            ?: tokens.firstOrNull()

        val displayAmount = when {
            ethEquivalentAmount > 0.0 -> ethEquivalentAmount
            else -> parseStakingTokenAmount(primaryToken)
        }

        val decimals = primaryToken?.decimals?.toIntOrNull() ?: 18

        val sumTokenUsd = tokens.sumOf { it.usdValue ?: 0.0 }

        // Prefer Moralis spot price when present; else implied USD / amount. valueUsd is always [totalUsd] below.
        val priceUsdPerUnit = when {
            primaryToken?.usdPrice != null && primaryToken.usdPrice > 0.0 ->
                primaryToken.usdPrice
            displayAmount > 0.0 && totalUsd > 0.0 ->
                totalUsd / displayAmount
            else -> null
        }
        val priceUsdStr = priceUsdPerUnit?.let { p ->
            BigDecimal.valueOf(p).stripTrailingZeros().toPlainString()
        } ?: "0"

        val displayName = listOfNotNull(
            stakeAccount.protocolName?.takeIf { it.isNotBlank() },
            position?.label?.takeIf { it.isNotBlank() },
        ).joinToString(" · ").ifBlank { primaryToken?.name ?: "Staking" }

        val symbol = primaryToken?.symbol?.takeIf { it.isNotBlank() } ?: "STAKE"

        val stakeKey = listOfNotNull(
            evmChainId,
            stakeAccount.protocolId ?: stakeAccount.protocolName,
            position?.address?.lowercase(),
            primaryToken?.contractAddress?.lowercase(),
        ).joinToString(":")

        val logo = stakeAccount.protocolLogo?.takeIf { it.isNotBlank() }
            ?: primaryToken?.logo?.takeIf { it.isNotBlank() }
            ?: primaryToken?.thumbnail?.takeIf { it.isNotBlank() }

        val uiAmountLine = buildDefiUiAmountLine(
            displayAmount = displayAmount,
            primarySymbol = primaryToken?.symbol,
            ethLikeSupply = ethLikeSupply,
            totalUsd = totalUsd,
            sumTokenUsd = sumTokenUsd,
        )

        val priceNativeStr = when {
            primaryToken?.tokenType?.equals("native", ignoreCase = true) == true -> "1"
            ethLikeSupply.isNotEmpty() -> "1"
            else -> "0"
        }
        val valueInQuoteStake = (priceNativeStr.toDoubleOrNull() ?: 0.0) * displayAmount
        val valueEthStake = when {
            ethUsdPrice != null && ethUsdPrice > 0.0 && totalUsd > 0.0 -> totalUsd / ethUsdPrice
            else -> (priceNativeStr.toDoubleOrNull() ?: 0.0) * displayAmount
        }

        return TokenAccount(
            pubkey = stakeKey,
            name = displayName,
            symbol = symbol,
            decimals = decimals,
            amountDouble = displayAmount,
            uiAmountString = uiAmountLine,
            priceUsd = priceUsdStr,
            valueUsd = totalUsd,
            valueNative = valueInQuoteStake,
            valueEthEquivalent = valueEthStake,
            priceNative = priceNativeStr,
            chainId = evmChainId,
            pairAddress = position?.address.orEmpty(),
            uri = stakeAccount.protocolUrl.orEmpty(),
            image = logo,
            description = buildStakingDescription(stakeAccount),
            createdOn = null,
            labels = buildList {
                add("DeFi")
                position?.label?.takeIf { it.isNotBlank() }?.let { add(it) }
            },
            baseToken = null,
            quoteToken = null,
            txns = null,
            volume = null,
            priceChange = null,
            liquidity = null,
            fdv = 0.0,
            marketCap = 0.0,
            pairCreatedAt = 0L,
            info = TokenMarketData(imageUrl = logo),
            tokenAccountCategory = TokenAccountCategories.DEFI,
        )
    }

    /** Moralis `balance_usd` is authoritative; fallback to summed token `usd_value`. */
    private fun resolvePositionTotalUsd(position: Position?): Double {
        if (position == null) return 0.0
        val fromPosition = position.balanceUsd?.takeIf { it > 0.0 } ?: 0.0
        if (fromPosition > 0.0) return fromPosition
        return position.tokens.orEmpty().sumOf { it.usdValue ?: 0.0 }
    }

    private fun isDebtLikeToken(t: PositionToken): Boolean {
        val ty = t.tokenType?.lowercase().orEmpty()
        return ty.contains("borrow") || ty.contains("debt") || ty.contains("loan")
    }

    /** WETH, aWETH, stETH, etc. — used to sum ETH-equivalent supply for display. */
    private fun isEthLikeSymbol(symbol: String?): Boolean {
        if (symbol.isNullOrBlank()) return false
        val u = symbol.uppercase()
        if (u == "ETH" || u == "WETH") return true
        if (u.length > 12) return false
        return u.endsWith("ETH")
    }

    private fun buildDefiUiAmountLine(
        displayAmount: Double,
        primarySymbol: String?,
        ethLikeSupply: List<PositionToken>,
        totalUsd: Double,
        sumTokenUsd: Double,
    ): String? {
        if (displayAmount <= 0.0) return null
        val sym = primarySymbol?.takeIf { it.isNotBlank() } ?: ""
        val mixed = totalUsd > 0.0 && sumTokenUsd > 0.0 &&
            abs(sumTokenUsd - totalUsd) > maxOf(0.5, 0.03 * totalUsd)
        val ethPart = formatDisplayAmount(displayAmount)
        return when {
            ethLikeSupply.isNotEmpty() -> {
                val head = "≈ $ethPart ETH"
                val tail = sym.takeIf { it.isNotEmpty() }?.let { " · $it" }.orEmpty()
                val mix = if (mixed) " · multi-asset" else ""
                "$head$tail$mix"
            }
            sym.isNotEmpty() -> "${formatDisplayAmount(displayAmount)} $sym"
            else -> formatDisplayAmount(displayAmount)
        }
    }

    private fun formatDisplayAmount(value: Double): String =
        BigDecimal.valueOf(value).setScale(8, RoundingMode.HALF_DOWN).stripTrailingZeros().toPlainString()

    private fun parseStakingTokenAmount(token: PositionToken?): Double {
        if (token == null) return 0.0
        token.balanceFormatted
            ?.replace(",", "")
            ?.trim()
            ?.toDoubleOrNull()
            ?.let { return it }
        val raw = token.balance ?: return 0.0
        val decimals = token.decimals?.toIntOrNull() ?: 18
        return try {
            BigDecimal(raw).movePointLeft(decimals).toDouble()
        } catch (_: Exception) {
            0.0
        }
    }

    private fun buildStakingDescription(stake: EvmStakingProtocol): String {
        val parts = mutableListOf<String>()
        stake.accountData?.healthFactor?.takeIf { it > 0.0 }?.let { hf ->
            parts += "Health ${BigDecimal.valueOf(hf).setScale(2, RoundingMode.HALF_UP).toPlainString()}"
        }
        stake.accountData?.netApy?.takeIf { abs(it) > 1e-9 }?.let { apy ->
            parts += "Net APY ${formatPercent(apy)}"
        }
        stake.position?.positionDetails?.apy?.takeIf { abs(it) > 1e-9 }?.let { apy ->
            parts += "APY ${formatPercent(apy)}"
        }
        stake.position?.totalUnclaimedUsdValue?.takeIf { it > 0.0 }?.let { usd ->
            parts += "Unclaimed ${formatUsd(usd)}"
        }
        stake.totalProjectedEarningsUsd?.yearly?.takeIf { it > 0.0 }?.let { usd ->
            parts += "Est. yearly ${formatUsd(usd)}"
        }
        return parts.joinToString(" · ")
    }

    private fun formatPercent(value: Double): String =
        "${BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).toPlainString()}%"

    private fun formatUsd(value: Double): String =
        "$${BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).toPlainString()}"

    // All others token items in the Evm wallet
    /**
     * @param ethUsdPrice ETH/USD spot for this Dexscreener chain (from native token quote). Used to convert
     * ERC-20 position value to **ETH equivalent** for portfolio `balanceNative` when the pool is USDC/VIRTUAL/etc.
     */
    fun createEvmTokenAccountItem(
        tokenAccountDetails: EvmTokenAccount,
        marketDataInfo: TokenMarketDataInfo?,
        evmChainId: String?,
        ethUsdPrice: Double?,
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

        val alchemyUsdNum = alchemyUsdPrice?.toDoubleOrNull()

        val priceUsd = when {
            tokenAccountDetails.isNative ->
                alchemyUsdPrice?.takeIf { it.isNotBlank() }
                    ?: marketDataInfo?.priceUsd
                    ?: "0"
            else -> marketDataInfo?.priceUsd ?: "0"
        }

        val priceUsdNum = priceUsd.toDoubleOrNull() ?: 0.0
        val valueUsdNum = when {
            tokenAccountDetails.isNative ->
                (alchemyUsdNum?.takeIf { it > 0.0 } ?: priceUsdNum) * uiAmount
            priceUsdNum > 0.0 -> priceUsdNum * uiAmount
            alchemyUsdNum != null && alchemyUsdNum > 0.0 -> alchemyUsdNum * uiAmount
            else -> 0.0
        }

        val priceNativeStr = if (tokenAccountDetails.isNative) {
            "1"
        } else {
            marketDataInfo?.priceNative ?: "0"
        }

        /** Shown in the row as value in quote (e.g. USDT), not ETH — avoids mislabeling ~0.4 ETH as USDT. */
        val valueInQuoteAsset = (priceNativeStr.toDoubleOrNull() ?: 0.0) * uiAmount

        val valueEthEquivalent = when {
            tokenAccountDetails.isNative -> null
            ethUsdPrice != null && ethUsdPrice > 0.0 && valueUsdNum > 0.0 ->
                valueUsdNum / ethUsdPrice
            isEthQuotedPair(marketDataInfo) ->
                (marketDataInfo?.priceNativeDouble ?: 0.0) * uiAmount
            else -> 0.0
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
            priceNative = priceNativeStr,
            priceUsd = priceUsd,
            valueUsd = valueUsdNum,
            valueNative = valueInQuoteAsset,
            valueEthEquivalent = valueEthEquivalent,
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

    /** Dexscreener [priceNative] is "how much quote token per 1 base"; only ETH-quoted pairs are already in ETH units. */
    private fun isEthQuotedPair(market: TokenMarketDataInfo?): Boolean {
        val sym = market?.quoteToken?.symbol?.uppercase() ?: return false
        if (sym == "ETH" || sym == "WETH") return true
        if (sym.length > 12) return false
        return sym.endsWith("ETH")
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