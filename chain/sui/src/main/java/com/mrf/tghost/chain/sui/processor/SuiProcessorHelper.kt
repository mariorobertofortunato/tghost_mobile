package com.mrf.tghost.chain.sui.processor

import com.mrf.tghost.chain.sui.domain.model.SuiCoin
import com.mrf.tghost.chain.sui.domain.model.SuiCoinMetadata
import com.mrf.tghost.chain.sui.domain.model.SuiObject
import com.mrf.tghost.chain.sui.domain.model.SuiOwnedObjectsSplit
import com.mrf.tghost.chain.sui.utils.asBigIntegerOrNull
import com.mrf.tghost.chain.sui.utils.extractCoinInnerType
import com.mrf.tghost.chain.sui.utils.flattenMoveObjectFields
import com.mrf.tghost.chain.sui.utils.getString
import com.mrf.tghost.chain.sui.utils.getStringDeep
import com.mrf.tghost.chain.sui.utils.graphQlBigIntToBigDecimal
import com.mrf.tghost.chain.sui.utils.isCoinType
import com.mrf.tghost.chain.sui.utils.isStakedSuiType
import com.mrf.tghost.chain.sui.utils.isSystemType
import com.mrf.tghost.chain.sui.utils.normalizeSuiMoveTypeRepr
import com.mrf.tghost.domain.model.SupportedChainId
import com.mrf.tghost.domain.model.TokenAccount
import com.mrf.tghost.domain.model.TokenAccountCategories
import com.mrf.tghost.domain.model.TokenMarketDataInfo
import com.mrf.tghost.domain.model.metadata.TokenOffChainMetadata
import java.net.URI
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

object SuiProcessorHelper {

    /**
     * Builds a staked-SUI row from an owned `0x3::staking_pool::StakedSui` move object.
     * Data comes from GraphQL [Object.contents](https://docs.sui.io/references/sui-graphql) (`json` / move fields).
     * `estimatedReward` is only non-zero if the node exposes it in `contents`; otherwise principal only.
     */
    fun createStakedSuiAccountItem(stakedSuiObject: SuiObject, suiPriceUsd: Double): TokenAccount? {
        val data = stakedSuiObject.data ?: return null
        val objectAddress = data.objectId ?: return null
        val repr = data.type.orEmpty()
        if (!isStakedSuiType(repr)) return null
        val rawFields = data.content?.fields ?: return null
        val flat = flattenMoveObjectFields(rawFields)
        flat.getString("pool_id") ?: return null
        val principal = parseStakePrincipalMist(flat) ?: return null
        val reward = flat["estimatedReward"]?.graphQlBigIntToBigDecimal() ?: BigDecimal.ZERO
        val totalMist = principal + reward
        if (totalMist <= BigDecimal.ZERO) return null

        val stakedSuiAmount = totalMist.toDouble() / 1_000_000_000.0
        val valueUsd = stakedSuiAmount * suiPriceUsd

        return TokenAccount(
            chainId = SupportedChainId.SUI.name,
            pubkey = objectAddress,
            pairAddress = "",
            name = "Staked SUI",
            symbol = "stSUI",
            uri = "",
            image = null,
            description = "Staked Sui",
            priceNative = "1",
            priceUsd = suiPriceUsd.toString(),
            txns = null,
            volume = null,
            priceChange = null,
            liquidity = null,
            fdv = 0.0,
            marketCap = 0.0,
            pairCreatedAt = 0L,
            info = null,
            amount = stakedSuiAmount.toString(), // Approximate raw amount
            decimals = 9,
            amountDouble = stakedSuiAmount,
            uiAmountString = stakedSuiAmount.toString(),
            valueUsd = valueUsd,
            valueNative = stakedSuiAmount,
            tokenAccountCategory = TokenAccountCategories.DEFI
        )
    }

    private fun parseStakePrincipalMist(flat: Map<String, JsonElement>): BigDecimal? {
        val el = flat["principal"] ?: return null
        el.graphQlBigIntToBigDecimal()?.let { return it }
        val obj = el as? JsonObject ?: return null
        val nested = flattenMoveObjectFields(obj)
        nested.getString("value")?.toBigDecimalOrNull()?.let { return it }
        return nested["value"]?.graphQlBigIntToBigDecimal()
    }

    fun createSuiCoinItem(
        tokenAccountDetails: SuiCoin,
        metadata: SuiCoinMetadata?,
        marketDataInfo: TokenMarketDataInfo?
    ): TokenAccount {
        val coinType = tokenAccountDetails.coinType
        val displayFields = tokenAccountDetails.renderedDisplay
        val decimals = metadata?.decimals ?: 9
        val rawBalance = tokenAccountDetails.totalBalance.toBigDecimalOrNull() ?: BigDecimal.ZERO
        val uiAmount = try {
            rawBalance.divide(BigDecimal.TEN.pow(decimals), MathContext.DECIMAL64).toDouble()
        } catch (e: Exception) {
            0.0
        }
        val priceUsd = marketDataInfo?.priceUsdDouble ?: 0.0
        val priceNative = marketDataInfo?.priceNativeDouble ?: 0.0
        val symbol = metadata?.symbol
            ?: displayFields.getString("symbol", "ticker")
            ?: coinType.substringAfterLast("::")
        val name = metadata?.name
            ?: displayFields.getString("name", "title")
            ?: symbol
        val logoUrl = marketDataInfo?.info?.imageUrl
            ?: metadata?.iconUrl
            ?: displayFields.getString("image_url", "image", "thumbnail_url")
        val description = metadata?.description ?: displayFields.getString("description")
        val uri = displayFields.getString("link", "project_url", "website").orEmpty()

        return TokenAccount(
            chainId = SupportedChainId.SUI.name,
            pubkey = coinType,
            pairAddress = marketDataInfo?.pairAddress,
            name = name,
            symbol = symbol,
            uri = uri,
            image = logoUrl,
            description = description,
            baseToken = marketDataInfo?.baseToken,
            quoteToken = marketDataInfo?.quoteToken,
            priceNative = priceNative.toString(),
            priceUsd = priceUsd.toString(),
            txns = marketDataInfo?.txns,
            volume = marketDataInfo?.volume,
            priceChange = marketDataInfo?.priceChange,
            liquidity = marketDataInfo?.liquidity,
            fdv = marketDataInfo?.fdv ?: 0.0,
            marketCap = marketDataInfo?.marketCap ?: 0.0,
            pairCreatedAt = marketDataInfo?.pairCreatedAt ?: 0,
            info = marketDataInfo?.info,
            amount = tokenAccountDetails.totalBalance,
            decimals = decimals,
            amountDouble = uiAmount,
            uiAmountString = uiAmount.toString(),
            tokenAccountCategory = TokenAccountCategories.HOLDINGS
        )
    }

    fun createSuiNftItem(
        nft: SuiObject,
        offChainMetadata: TokenOffChainMetadata? = null
    ): TokenAccount {
        val objectId = nft.data?.objectId.orEmpty()
        val digest = nft.data?.digest.orEmpty()
        val rawType = nft.data?.type.orEmpty()
        val normalizedType = rawType.takeIf { it.isNotBlank() }?.let(::normalizeSuiMoveTypeRepr) ?: ""
        val fields = flattenMoveObjectFields(nft.data?.content?.fields.orEmpty()).toMutableMap()
        val displayFields = nft.data?.renderedDisplay.orEmpty()

        val onChainName = displayFields.getString("name", "title", "display_name", "displayName")
            ?: fields.getString(
            "name", "Name", "title", "Title", "display_name", "displayName"
        )
        val onChainDesc = displayFields.getString("description", "desc")
            ?: fields.getString(
            "description", "Description", "desc", "bio", "Bio"
        )
        val rawMedia = displayFields.getString(
            "image_url", "imageUrl", "image", "thumbnail_url", "thumbnailUrl"
        ) ?: fields.getStringDeep(
            "image_url", "imageUrl", "image", "img_url", "imgUrl",
            "thumbnail_url", "thumbnailUrl", "animation_url", "animationUrl",
            "media_url", "mediaUrl", "file", "url"
        )
        val indexOrTokenId = fields.getStringDeep("index", "token_id", "tokenId", "edition", "Edition")

        val shortType = normalizedType.substringAfterLast("::").ifBlank { normalizedType }
        val symbol = shortType.ifBlank { "NFT" }

        val name = when {
            !onChainName.isNullOrBlank() -> onChainName
            !offChainMetadata?.name.isNullOrBlank() -> offChainMetadata.name
            !indexOrTokenId.isNullOrBlank() -> "$symbol #$indexOrTokenId"
            shortType.isNotBlank() -> shortType
            else -> "NFT"
        }

        val offChainImage = offChainMetadata?.image?.takeIf { it.isNotBlank() }
            ?: offChainMetadata?.properties?.files
                ?.firstOrNull { file ->
                    val type = file.type.orEmpty().lowercase()
                    type.startsWith("image/")
                }?.uri
                ?.takeIf { it.isNotBlank() }

        val image = rawMedia?.trim()?.takeIf { it.isNotEmpty() }
            ?: offChainImage

        val normalizedUri = extractNftUriForTokenAccount(nft)
        val createdOn = fields.getStringDeep(
            "created_at", "createdAt", "minted_at", "mintedAt", "timestamp", "time"
        ) ?: displayFields.getString("created_at", "createdAt")

        val description = buildNftDescription(
            onChainDesc = onChainDesc,
            metadataDesc = offChainMetadata?.description,
            fields = fields,
            normalizedType = normalizedType,
            objectId = objectId
        )
        val nftAmount = extractNftAmount(fields)

        val labels = buildList {
            add("Sui")
            normalizedType.split("::").getOrNull(1)?.takeIf { it.isNotBlank() }?.let { add(it) }
            displayFields.getString("creator")?.takeIf { it.isNotBlank() }?.let { add(it) }
            normalizedUri.takeIf { it.isNotBlank() }?.let { url ->
                try {
                    URI(url).host?.removePrefix("www.")?.takeIf { it.isNotBlank() }?.let { add(it) }
                } catch (_: Exception) {
                    Unit
                }
            }
            if (nft.data?.content?.hasPublicTransfer == false) add("Non-transferable")
        }

        return TokenAccount(
            chainId = SupportedChainId.SUI.name,
            pubkey = objectId.ifBlank {
                digest.ifBlank { normalizedType.ifBlank { nft.hashCode().toString() } }
            },
            name = name,
            symbol = symbol,
            uri = normalizedUri,
            image = image,
            description = description,
            createdOn = createdOn,
            labels = labels,
            decimals = 0,
            amount = nftAmount.rawAmount,
            amountDouble = nftAmount.amountDouble,
            uiAmountString = nftAmount.uiAmountString,
            tokenAccountCategory = TokenAccountCategories.NFTS
        )
    }

    private data class NftAmount(
        val rawAmount: String,
        val amountDouble: Double?,
        val uiAmountString: String
    )

    private fun extractNftAmount(fields: Map<String, JsonElement>): NftAmount {
        val candidate = fields.getStringDeep(
            "amount", "balance", "quantity", "qty", "principal"
        )?.trim()
        val numeric = candidate
            ?.takeIf { it.matches(Regex("^-?\\d+(\\.\\d+)?$")) }
            ?.toDoubleOrNull()
            ?.takeIf { it.isFinite() }
        return if (!candidate.isNullOrBlank()) {
            NftAmount(
                rawAmount = candidate,
                amountDouble = numeric,
                uiAmountString = candidate
            )
        } else {
            NftAmount(
                rawAmount = "1",
                amountDouble = 1.0,
                uiAmountString = "1"
            )
        }
    }

    fun extractNftMetadataUri(nft: SuiObject): String {
        val fields = flattenMoveObjectFields(nft.data?.content?.fields.orEmpty())
        return (
            fields.getStringDeep("metadata_url", "metadataUrl", "uri", "url")
            )?.trim().orEmpty()
    }

    private fun extractNftUriForTokenAccount(nft: SuiObject): String {
        val fields = flattenMoveObjectFields(nft.data?.content?.fields.orEmpty())
        val displayFields = nft.data?.renderedDisplay.orEmpty()
        return (
            fields.getStringDeep("metadata_url", "metadataUrl", "uri", "url", "external_url", "externalUrl")
                ?: displayFields.getString("link", "project_url", "projectUrl")
            )?.trim().orEmpty()
    }

    private fun buildNftDescription(
        onChainDesc: String?,
        metadataDesc: String?,
        fields: Map<String, JsonElement>,
        normalizedType: String,
        objectId: String
    ): String {
        val main = onChainDesc?.takeIf { it.isNotBlank() }
            ?: metadataDesc?.takeIf { it.isNotBlank() }
        val extras = buildList {
            fields.getString("xcetus_balance")?.let { add("xCETUS: $it") }
            fields.getString("principal")?.let { add("Principal (MIST): $it") }
            fields.getString("position_id")?.let { add("Position: $it") }
            fields.getString("obligation_id")?.let { add("Obligation: $it") }
            fields.getString("pool_id")?.let { add("Pool: $it") }
        }
        return buildString {
            if (!main.isNullOrBlank()) {
                append(main.trim())
            }
            extras.forEach { line ->
                if (isNotEmpty()) append('\n')
                append(line)
            }
            if (isEmpty()) {
                if (normalizedType.isNotBlank()) append(normalizedType)
                val shortId = shortenObjectIdForDisplay(objectId)
                if (shortId != null) {
                    if (isNotEmpty()) append(" · ")
                    append(shortId)
                }
            }
        }.trim()
    }

    private fun shortenObjectIdForDisplay(objectId: String): String? {
        if (objectId.length <= 14) return objectId.takeIf { it.isNotBlank() }
        return "${objectId.take(8)}…${objectId.takeLast(6)}"
    }

    fun splitOwnedObjects(objects: List<SuiObject>): SuiOwnedObjectsSplit {
        data class Acc(
            val sum: BigInteger,
            val count: Int,
            val renderedDisplay: Map<String, JsonElement>
        )

        val byType = linkedMapOf<String, Acc>()
        val nftObjects = mutableListOf<SuiObject>()
        val stakedSuiObjects = mutableListOf<SuiObject>()
        for (obj in objects) {
            when {
                isCoin(obj) -> {
                    val fullType = obj.data?.type ?: continue
                    val innerType = extractCoinInnerType(fullType)?.let(::normalizeSuiMoveTypeRepr)
                        ?: continue
                    val fields = obj.data.content?.fields ?: continue
                    val balance = readCoinBalance(fields) ?: continue
                    val currentDisplay = obj.data.renderedDisplay
                    val acc = byType[innerType] ?: Acc(BigInteger.ZERO, 0, emptyMap())
                    byType[innerType] = Acc(
                        sum = acc.sum + balance,
                        count = acc.count + 1,
                        renderedDisplay = acc.renderedDisplay.ifEmpty { currentDisplay }
                    )
                }
                isStakedSui(obj) -> stakedSuiObjects.add(obj)
                isNft(obj) -> nftObjects.add(obj)
                else -> Unit
            }
        }
        val coins = byType.map { (coinType, acc) ->
            SuiCoin(
                coinType = coinType,
                coinObjectCount = acc.count,
                totalBalance = acc.sum.toString(),
                lockedBalance = null,
                renderedDisplay = acc.renderedDisplay
            )
        }
        return SuiOwnedObjectsSplit(
            coinObjects = coins,
            nftObjects = nftObjects,
            stakedSuiObjects = stakedSuiObjects
        )
    }


    private fun readCoinBalance(fields: Map<String, JsonElement>): BigInteger? {
        fields["balance"]?.asBigIntegerOrNull()?.let { return it }
        val nested = fields["fields"] as? JsonObject ?: return null
        return nested["balance"]?.asBigIntegerOrNull()
    }

    private fun isCoin(obj: SuiObject): Boolean {
        val t = obj.data?.type ?: return false
        return isCoinType(t)
    }

    private fun isStakedSui(obj: SuiObject): Boolean {
        val t = obj.data?.type ?: return false
        return isStakedSuiType(t)
    }

    private fun isSystemObject(obj: SuiObject): Boolean {
        val t = obj.data?.type ?: return false
        return isSystemType(t)
    }

    fun isNft(obj: SuiObject): Boolean {
        val type = obj.data?.type ?: return false
        if (type.isBlank()) return false
        return !isCoin(obj)
                && !isStakedSui(obj)
                && !isSystemObject(obj)
    }

}
