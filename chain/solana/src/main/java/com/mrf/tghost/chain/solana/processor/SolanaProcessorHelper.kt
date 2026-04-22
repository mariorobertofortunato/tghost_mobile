package com.mrf.tghost.chain.solana.processor

import com.mrf.tghost.domain.model.TokenMarketDataInfo
import com.mrf.tghost.domain.model.Token
import com.mrf.tghost.domain.model.TokenAccount
import com.mrf.tghost.domain.model.TokenAccountCategories
import com.mrf.tghost.domain.model.TokenMarketData
import com.mrf.tghost.domain.model.metadata.TokenCombinedMetadata
import com.mrf.tghost.chain.solana.domain.model.DasItem
import com.mrf.tghost.chain.solana.domain.model.SolanaSplTokenAccount
import com.mrf.tghost.chain.solana.utils.LAMPORTS_IN_SOL
import com.mrf.tghost.chain.solana.utils.METADATA_PROGRAM_ID_STRING_FOR_TMU
import com.mrf.tghost.chain.solana.utils.SOLANA_STAKE_PROGRAM_ID
import com.mrf.tghost.chain.solana.utils.SOLANA_TOKEN_MINT
import com.mrf.tghost.domain.model.TokenHelpers
import org.sol4k.PublicKey
import java.math.BigDecimal
import java.security.MessageDigest

object SolanaProcessorHelper {

    // Native (liquid) SOL item
    fun createSolanaNativeTokenAccountItem(
        solanaQuote: TokenMarketDataInfo?,
        solBalance: Double
    ): TokenAccount {
        return TokenAccount(
            pubkey = SOLANA_TOKEN_MINT,
            name = "Solana",
            symbol = "SOL",
            uri = solanaQuote?.url ?: "",
            amount = solBalance.toString(),
            decimals = 9,
            amountDouble = solBalance,
            uiAmountString = solBalance.toString(),
            image = null,
            description = "Solana",
            createdOn = null,
            chainId = solanaQuote?.chainId ?: "solana",
            pairAddress = solanaQuote?.pairAddress ?: "",
            labels = emptyList(),
            priceUsd = solanaQuote?.priceUsd ?: "1",
            fdv = solanaQuote?.fdv ?: 0.0,
            marketCap = solanaQuote?.marketCap ?: 0.0,
            pairCreatedAt = solanaQuote?.pairCreatedAt ?: 0L,
            info = solanaQuote?.info,
            baseToken = solanaQuote?.quoteToken,
            quoteToken = solanaQuote?.baseToken,
            txns = solanaQuote?.txns,
            volume = solanaQuote?.volume,
            priceChange = solanaQuote?.priceChange,
            liquidity = solanaQuote?.liquidity,
            tokenAccountCategory = TokenAccountCategories.HOLDINGS
        )
    }

    // Stacked SOL item
    fun createStakedSolAccountItem(
        stakedSolAmount: BigDecimal,
        solPriceUsd: Double
    ): TokenAccount {
        val totalStakedLamports = (stakedSolAmount * LAMPORTS_IN_SOL).toLong()
        val stakedValueUsd = stakedSolAmount.toDouble() * solPriceUsd

        return TokenAccount(
            pubkey = SOLANA_STAKE_PROGRAM_ID,
            name = "Staked SOL",
            symbol = "stSOL",
            amount = totalStakedLamports.toString(),
            decimals = 9,
            amountDouble = stakedSolAmount.toDouble(),
            uiAmountString = stakedSolAmount.toString(),
            priceUsd = solPriceUsd.toString(),
            valueUsd = stakedValueUsd,
            valueNative = stakedSolAmount.toDouble(),
            chainId = "solana",
            pairAddress = "",
            uri = "",
            image = null,
            description = "Native Solana Stake",
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

    // All others token items in the Solana wallet
    fun createSolanaTokenAccountItem(
        tokenAccountDetails: SolanaSplTokenAccount?,
        metadata: TokenCombinedMetadata? = null,
        marketDataInfo: TokenMarketDataInfo?,
        priceUsdSolana: String = "1"
    ): TokenAccount {
        return TokenAccount(
            pubkey = tokenAccountDetails?.pubkey ?: "",
            name = metadata?.name ?: marketDataInfo?.baseToken?.name ?: "Unknown",
            symbol = metadata?.symbol ?: marketDataInfo?.baseToken?.symbol ?: "?",
            uri = metadata?.uri ?: "",
            amount = tokenAccountDetails?.account?.data?.parsed?.info?.tokenAmount?.amount ?: "0",
            decimals = tokenAccountDetails?.account?.data?.parsed?.info?.tokenAmount?.decimals ?: 0,
            amountDouble = tokenAccountDetails?.account?.data?.parsed?.info?.tokenAmount?.uiAmount,
            uiAmountString = tokenAccountDetails?.account?.data?.parsed?.info?.tokenAmount?.uiAmountString,
            image = metadata?.image ?: marketDataInfo?.info?.imageUrl,
            description = metadata?.description,
            createdOn = metadata?.createdOn,
            chainId = marketDataInfo?.chainId ?: "",
            pairAddress = marketDataInfo?.pairAddress ?: "",
            labels = marketDataInfo?.labels ?: emptyList(),
            baseToken = marketDataInfo?.baseToken,
            quoteToken = convertUSDQuoteTokenToSolIfNeeded(marketDataInfo?.quoteToken),
            priceNative = convertUSDPriceNativeToSolIfNeeded(
                quoteToken = marketDataInfo?.quoteToken,
                priceNative = marketDataInfo?.priceNative,
                priceUsdSolana = priceUsdSolana
            ),
            priceUsd = marketDataInfo?.priceUsd ?: "0",
            txns = marketDataInfo?.txns,
            volume = marketDataInfo?.volume,
            priceChange = marketDataInfo?.priceChange,
            liquidity = marketDataInfo?.liquidity,
            fdv = marketDataInfo?.fdv ?: 0.0,
            marketCap = marketDataInfo?.marketCap ?: 0.0,
            pairCreatedAt = marketDataInfo?.pairCreatedAt ?: 0,
            info = marketDataInfo?.info ?: TokenMarketData(),
            tokenAccountCategory = TokenHelpers.getTokenAccountCategory(
                metadata?.name ?: marketDataInfo?.baseToken?.name ?: "Unknown",
            )
        )
    }

    fun createSolanaNftAccountItem(
        dasAsset: DasItem
    ): TokenAccount {
        return TokenAccount(
            pubkey = dasAsset.id,
            name = dasAsset.content?.metadata?.name ?: "Unknown NFT",
            symbol = dasAsset.content?.metadata?.symbol ?: "",
            uri = dasAsset.content?.jsonUri ?: "",
            amount = "1",
            decimals = 0,
            amountDouble = 1.0,
            uiAmountString = "1",
            image = dasAsset.content?.files?.firstOrNull()?.uri
                ?: dasAsset.content?.files?.firstOrNull()?.cdnUri,
            description = dasAsset.content?.metadata?.description,
            createdOn = null,
            chainId = "solana",
            pairAddress = "",
            labels = listOf("NFT"),
            priceNative = "0",
            priceUsd = "0",
            tokenAccountCategory = TokenAccountCategories.NFTS
        )
    }

    fun createMetadataPDAString(mintAddressString: String): String {
        try {
            val mintPubkeySol4k = PublicKey(mintAddressString)
            val metadataProgramIdSol4k = PublicKey(METADATA_PROGRAM_ID_STRING_FOR_TMU)
            val (pdaInfoSol4k, _) = findMetadataPda(mintPubkeySol4k, metadataProgramIdSol4k)
            val metadataPDAString = pdaInfoSol4k.toBase58()
            return metadataPDAString
        } catch (e: Exception) {
            return ""
        }
    }

    private fun findMetadataPda(
        mintPubkey: PublicKey,
        metadataProgramId: PublicKey
    ): Pair<PublicKey, Int> {
        val seeds = listOf(
            "metadata".encodeToByteArray(),
            metadataProgramId.bytes(),
            mintPubkey.bytes()
        )

        for (bumpSeed in 255 downTo 0) {
            try {
                val seedsWithBump = seeds + byteArrayOf(bumpSeed.toByte())
                val hash = MessageDigest.getInstance("SHA-256")
                seedsWithBump.forEach { hash.update(it) }
                hash.update(metadataProgramId.bytes())
                hash.update("ProgramDerivedAddress".encodeToByteArray())

                val hashBytes = hash.digest()

                val publicKey = PublicKey(hashBytes.sliceArray(0..31))
                return Pair(publicKey, bumpSeed)
            } catch (e: Exception) {
                continue
            }
        }
        throw RuntimeException("Unable to find PDA")
    }

    fun convertUSDtoSol(priceUsdToken: String?, priceUsdSolana: String?): String {
        val priceSol = priceUsdToken?.toDouble()?.div(priceUsdSolana?.toDouble() ?: 1.0)
        return priceSol.toString()
    }

    fun convertUSDQuoteTokenToSolIfNeeded(quoteToken: Token?): Token? {
        return if (quoteToken?.symbol?.contains("USD") == true) {
            Token(
                address = "So11111111111111111111111111111111111111112",
                name = "Wrapped SOL",
                symbol = "SOL"
            )
        } else {
            quoteToken
        }
    }

    fun convertUSDPriceNativeToSolIfNeeded(
        quoteToken: Token?,
        priceNative: String?,
        priceUsdSolana: String?
    ): String {
        return if (quoteToken?.symbol?.contains("USD") == true) {
            if (priceNative != null) {
                convertUSDtoSol(priceNative, priceUsdSolana ?: "1")
            } else {
                "0"
            }
        } else {
            priceNative ?: "0"
        }
    }

    fun isValidNft(asset: DasItem): Boolean {

        val interfaceName = asset.interfaceName
        var isSpamImageUri = false
        var isSpamCdnUri = false
        val isInterfaceNameSuspect =
            interfaceName?.contains("nftdrop", ignoreCase = true) == true ||
                    interfaceName?.contains("reward", ignoreCase = true) == true ||
                    interfaceName?.contains("claim", ignoreCase = true) == true ||
                    interfaceName?.contains(".today", ignoreCase = true) == true ||
                    interfaceName?.contains("pastebin", ignoreCase = true) == true ||
                    interfaceName?.contains("onlinehostingipfs", ignoreCase = true) == true ||
                    interfaceName?.contains("gift", ignoreCase = true) == true || // This checks is risky, we might be excluding proper NFt called "gift" or something
                    interfaceName?.contains("redeem", ignoreCase = true) == true ||
                    interfaceName?.contains("$", ignoreCase = true) == true


        asset.content?.files?.size?.let {
            if (it > 0) {
                val imageUrl = asset.content.files[0].uri ?: ""
                isSpamImageUri =
                    imageUrl.contains("nftdrop", ignoreCase = true) ||
                            imageUrl.contains("reward", ignoreCase = true) ||
                            imageUrl.contains("claim", ignoreCase = true) ||
                            imageUrl.contains(".today", ignoreCase = true) ||
                            imageUrl.contains("pastebin", ignoreCase = true) ||
                            imageUrl.contains("onlinehostingipfs", ignoreCase = true)
                val cdnUri = asset.content.files[0].cdnUri ?: ""
                isSpamCdnUri =
                    cdnUri.contains("nftdrop") ||
                            cdnUri.contains("reward", ignoreCase = true) ||
                            cdnUri.contains("claim", ignoreCase = true) ||
                            cdnUri.contains(".today", ignoreCase = true) ||
                            cdnUri.contains("pastebin", ignoreCase = true) ||
                            cdnUri.contains("onlinehostingipfs", ignoreCase = true)
            }
        }


        val jsonUri = asset.content?.jsonUri ?: ""
        val isSpamJsonUri =
            jsonUri.contains("nftdrop", ignoreCase = true) ||
                    jsonUri.contains("reward", ignoreCase = true) ||
                    jsonUri.contains("claim", ignoreCase = true) ||
                    jsonUri.contains(".today", ignoreCase = true) ||
                    jsonUri.contains("pastebin", ignoreCase = true) ||
                    jsonUri.contains("onlinehostingipfs", ignoreCase = true)


        return (
                !isInterfaceNameSuspect &&
                        !isSpamImageUri &&
                        !isSpamCdnUri &&
                        !isSpamJsonUri
                )
    }


}