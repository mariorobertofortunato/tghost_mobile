package com.mrf.tghost.chain.sui.utils

private const val COIN_STRUCT_MARKER = "::coin::Coin<"
private const val STAKED_SUI_TYPE_MARKER = "::staking_pool::StakedSui"

internal fun extractCoinInnerType(fullType: String): String? {
    val idx = fullType.indexOf(COIN_STRUCT_MARKER)
    if (idx < 0 || !fullType.endsWith('>')) return null
    val start = idx + COIN_STRUCT_MARKER.length
    return fullType.substring(start, fullType.length - 1).trim()
}

internal fun normalizeSuiMoveTypeRepr(repr: String): String =
    Regex("0x[0-9a-fA-F]+").replace(repr) { shortenHexAddress(it.value) }

internal fun isCoinType(typeRepr: String): Boolean = typeRepr.contains(COIN_STRUCT_MARKER)

internal fun isStakedSuiType(typeRepr: String): Boolean = typeRepr.contains(STAKED_SUI_TYPE_MARKER)

internal fun isSystemType(typeRepr: String): Boolean {
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
    return markers.any { typeRepr.contains(it) }
}

private fun shortenHexAddress(hexWithPrefix: String): String {
    if (!hexWithPrefix.startsWith("0x", ignoreCase = true)) return hexWithPrefix
    val body = hexWithPrefix.drop(2).trimStart('0').ifEmpty { "0" }
    return "0x$body"
}
