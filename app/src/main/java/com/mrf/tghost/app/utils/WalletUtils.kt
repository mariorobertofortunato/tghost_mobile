package com.mrf.tghost.app.utils

import com.mrf.tghost.domain.model.SupportedChain

fun shrinkAddress(
    address: String,
    prefixLength: Int = 6,
    suffixLength: Int = 4,
    separator: String = "..."
): String {
    if (address.length <= prefixLength + suffixLength + separator.length) {
        return address
    }
    val prefix = address.take(prefixLength)
    val suffix = address.takeLast(suffixLength)
    return "$prefix$separator$suffix"
}


fun formatWalletDisplay(
    address: String,
    prefixLength: Int = 6,
    suffixLength: Int = 4,
    addressSeparator: String = "..."
): String {
    val shrunkAddress = shrinkAddress(address, prefixLength, suffixLength, addressSeparator)
    return shrunkAddress
}



fun isValidKey(key: String, selectedChain: String): Boolean {
    return when (selectedChain) {
        SupportedChain.SOLANA.name -> {
            checkSolanaKeyValidity(key)
        }

        SupportedChain.EVM.name -> {
            checkEvmKeyValidity(key)
        }

        SupportedChain.SUI.name -> {
            checkSuiKeyValidity(key)
        }

        SupportedChain.TEZ.name -> {
            checkTezosKeyValidity(key)
        }

        else -> false
    }
}

fun checkSolanaKeyValidity(key: String): Boolean {
    //if (checkTezosKeyValidity(key)) return false
    val regex = Regex("^[1-9A-HJ-NP-Za-km-z]{32,44}$")
    return regex.matches(key)
}

fun checkEvmKeyValidity(key: String): Boolean {
    val regex = Regex("^0x[a-fA-F0-9]{40}$")
    return regex.matches(key)
}

fun checkSuiKeyValidity(key: String): Boolean {
    val regex = Regex("^0x[a-fA-F0-9]{64}$")
    return regex.matches(key)
}

fun checkTezosKeyValidity(key: String): Boolean {
    val regex = Regex("^(tz1|tz2|tz3|KT1)[1-9A-HJ-NP-Za-km-z]{33}$")
    return regex.matches(key)
}
