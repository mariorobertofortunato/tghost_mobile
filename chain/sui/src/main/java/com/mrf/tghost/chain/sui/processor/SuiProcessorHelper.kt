package com.mrf.tghost.chain.sui.processor

import com.mrf.tghost.chain.sui.domain.model.SuiCoin
import com.mrf.tghost.chain.sui.domain.model.SuiCoinMetadata
import com.mrf.tghost.chain.sui.domain.model.SuiObject
import com.mrf.tghost.domain.model.SupportedChainId
import com.mrf.tghost.domain.model.TokenAccount
import com.mrf.tghost.domain.model.TokenAccountCategories
import com.mrf.tghost.domain.model.TokenMarketDataInfo
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.longOrNull

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
        if (!repr.contains(STAKED_SUI_TYPE_MARKER)) return null
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

    private fun JsonElement.graphQlBigIntToBigDecimal(): BigDecimal? {
        return when (this) {
            is JsonPrimitive ->
                contentOrNull?.toBigDecimalOrNull() ?: longOrNull?.toBigDecimal()
            else -> null
        }
    }

    fun createSuiCoinItem(
        tokenAccountDetails: SuiCoin,
        metadata: SuiCoinMetadata?,
        marketDataInfo: TokenMarketDataInfo?
    ): TokenAccount {
        val coinType = tokenAccountDetails.coinType
        val decimals = metadata?.decimals ?: 9
        val rawBalance = tokenAccountDetails.totalBalance.toBigDecimalOrNull() ?: BigDecimal.ZERO
        val uiAmount = try {
            rawBalance.divide(BigDecimal.TEN.pow(decimals), MathContext.DECIMAL64).toDouble()
        } catch (e: Exception) {
            0.0
        }
        val priceUsd = marketDataInfo?.priceUsdDouble ?: 0.0
        val priceNative = marketDataInfo?.priceNativeDouble ?: 0.0
        val symbol = metadata?.symbol ?: coinType.substringAfterLast("::")
        val name = metadata?.name ?: symbol
        val logoUrl = marketDataInfo?.info?.imageUrl ?: metadata?.iconUrl
        val description = metadata?.description

        return TokenAccount(
            chainId = SupportedChainId.SUI.name,
            pubkey = coinType,
            pairAddress = marketDataInfo?.pairAddress,
            name = name,
            symbol = symbol,
            uri = "",
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
        metadata: SuiCoinMetadata?,
    ): TokenAccount {
        val objectId = nft.data?.objectId.orEmpty()
        val rawType = nft.data?.type.orEmpty()
        val normalizedType = rawType.takeIf { it.isNotBlank() }?.let(::normalizeSuiMoveTypeRepr) ?: ""
        val fields = flattenMoveObjectFields(nft.data?.content?.fields.orEmpty())

        val onChainName = fields.getString(
            "name", "Name", "title", "Title", "display_name", "displayName"
        )
        val onChainDesc = fields.getString(
            "description", "Description", "desc", "bio", "Bio"
        )
        val rawMedia = fields.getString(
            "image_url", "imageUrl", "image", "img_url", "imgUrl", "url", "uri",
            "thumbnail_url", "thumbnailUrl", "animation_url", "animationUrl"
        )
        val indexOrTokenId = fields.getString("index", "token_id", "tokenId", "edition", "Edition")

        val shortType = normalizedType.substringAfterLast("::").ifBlank { normalizedType }
        val symbol = metadata?.symbol?.takeIf { it.isNotBlank() } ?: shortType.ifBlank { "NFT" }

        val name = when {
            !onChainName.isNullOrBlank() -> onChainName
            !metadata?.name.isNullOrBlank() -> metadata!!.name
            !indexOrTokenId.isNullOrBlank() -> "$symbol #$indexOrTokenId"
            shortType.isNotBlank() -> shortType
            else -> "NFT"
        }

        val image = rawMedia?.trim()?.takeIf { it.isNotEmpty() }
            ?: metadata?.iconUrl?.takeIf { it.isNotEmpty() }

        val uri = fields.getString("url", "uri", "external_url", "externalUrl")
            ?.trim()
            .orEmpty()

        val description = buildNftDescription(
            onChainDesc = onChainDesc,
            metadataDesc = metadata?.description,
            fields = fields,
            normalizedType = normalizedType,
            objectId = objectId
        )

        val labels = buildList {
            add("Sui")
            normalizedType.split("::").getOrNull(1)?.takeIf { it.isNotBlank() }?.let { add(it) }
        }

        return TokenAccount(
            chainId = SupportedChainId.SUI.name,
            pubkey = objectId.ifBlank { normalizedType },
            name = name,
            symbol = symbol,
            uri = uri,
            image = image,
            description = description,
            labels = labels,
            decimals = metadata?.decimals?.takeIf { it > 0 } ?: 0,
            amount = "1",
            amountDouble = 1.0,
            uiAmountString = "1",
            tokenAccountCategory = TokenAccountCategories.NFTS
        )
    }

    private fun flattenMoveObjectFields(fields: Map<String, JsonElement>): Map<String, JsonElement> {
        val inner = (fields["fields"] as? JsonObject)?.jsonObject?.toMap().orEmpty()
        if (inner.isEmpty()) return fields
        return inner + fields.filterKeys { it != "fields" }
    }

    private fun Map<String, JsonElement>.getString(vararg keys: String): String? {
        for (key in keys) {
            val el = this[key] ?: continue
            if (el is JsonNull) continue
            val prim = el as? JsonPrimitive ?: continue
            val text = prim.contentOrNull?.trim()?.takeIf { it.isNotEmpty() } ?: continue
            return text
        }
        return null
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

    /**
     * Single pass over owned objects: aggregate `Coin<>`, native `StakedSui`, NFT candidates.
     * Other system / protocol objects are skipped.
     */
    data class OwnedObjectsSplit(
        val coinObjects: List<SuiCoin>,
        val nftObjects: List<SuiObject>,
        val stakedSuiObjects: List<SuiObject>
    )

    fun splitOwnedObjects(objects: List<SuiObject>): OwnedObjectsSplit {
        data class Acc(val sum: BigInteger, val count: Int)

        val byType = linkedMapOf<String, Acc>()
        val nftObjects = mutableListOf<SuiObject>()
        val stakedSuiObjects = mutableListOf<SuiObject>()
        for (obj in objects) {
            when {
                isCoin(obj) -> {
                    val fullType = obj.data?.type ?: continue
                    val innerType = extractCoinInnerType(fullType)?.let(::normalizeSuiMoveTypeRepr)
                        ?: continue
                    val fields = obj.data?.content?.fields ?: continue
                    val balance = readCoinBalance(fields) ?: continue
                    val acc = byType[innerType] ?: Acc(BigInteger.ZERO, 0)
                    byType[innerType] = Acc(acc.sum + balance, acc.count + 1)
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
                lockedBalance = null
            )
        }
        return OwnedObjectsSplit(
            coinObjects = coins,
            nftObjects = nftObjects,
            stakedSuiObjects = stakedSuiObjects
        )
    }


    /** GraphQL `type.repr` uses 32-byte hex addresses; short RPC form uses `0x2::…`. */
    private const val COIN_STRUCT_MARKER = "::coin::Coin<"

    private fun extractCoinInnerType(fullType: String): String? {
        val idx = fullType.indexOf(COIN_STRUCT_MARKER)
        if (idx < 0 || !fullType.endsWith('>')) return null
        val start = idx + COIN_STRUCT_MARKER.length
        return fullType.substring(start, fullType.length - 1).trim()
    }

    /** Collapses `0x000…02` → `0x2` so coin metadata / market APIs match canonical types. */
    private fun normalizeSuiMoveTypeRepr(repr: String): String =
        Regex("0x[0-9a-fA-F]+").replace(repr) { shortenHexAddress(it.value) }

    private fun shortenHexAddress(hexWithPrefix: String): String {
        if (!hexWithPrefix.startsWith("0x", ignoreCase = true)) return hexWithPrefix
        val body = hexWithPrefix.drop(2).trimStart('0').ifEmpty { "0" }
        return "0x$body"
    }

    private fun readCoinBalance(fields: Map<String, JsonElement>): BigInteger? {
        fields["balance"]?.asBigIntegerOrNull()?.let { return it }
        val nested = fields["fields"] as? JsonObject ?: return null
        return nested["balance"]?.asBigIntegerOrNull()
    }

    private fun JsonElement.asBigIntegerOrNull(): BigInteger? {
        val p = this as? JsonPrimitive ?: return null
        return p.content.toBigIntegerOrNull()
            ?: p.content.toLongOrNull()?.toBigInteger()
    }

    private fun isCoin(obj: SuiObject): Boolean {
        val t = obj.data?.type ?: return false
        return t.contains(COIN_STRUCT_MARKER)
    }

    private const val STAKED_SUI_TYPE_MARKER = "::staking_pool::StakedSui"

    private fun isStakedSui(obj: SuiObject): Boolean {
        val t = obj.data?.type ?: return false
        return t.contains(STAKED_SUI_TYPE_MARKER)
    }

    private fun isProtocolObject(obj: SuiObject): Boolean {
        val patterns = listOf(
            "::DepositInfo",
            "::position::Position",
            "::position::PositionCap",
            "::ObligationOwnerCap",
            "::lpcoin::",
            "::pool::",
            "::clmm::",      // Cetus / Turbos
            "::vault::",
            "::amm::"
        )

        return patterns.any { obj.data?.type?.contains(it) == true }
    }

    private fun isSystemObject(obj: SuiObject): Boolean {
        val t = obj.data?.type ?: return false
        val markers = listOf(
            "::kiosk::",
            "::package::",
            "::clock::",
            "::stake::",
            "::transfer_policy::",
            "::oblivious_access::",
            "::display::",
            "::dynamic_field::"
        )
        return markers.any { t.contains(it) }
    }

    fun isNft(obj: SuiObject): Boolean {
        return !isCoin(obj)
                && !isStakedSui(obj)
                && !isSystemObject(obj)
                && !isProtocolObject(obj)
    }

}