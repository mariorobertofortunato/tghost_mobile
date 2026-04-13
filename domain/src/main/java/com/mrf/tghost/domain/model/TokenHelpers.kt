package com.mrf.tghost.domain.model

object TokenHelpers {

    fun getTokenAccountCategory(name: String): TokenAccountCategories {
        return when {
            name.contains("staking", ignoreCase = true) ||
                    name.contains("staked", ignoreCase = true) ||
                    name.contains("stake", ignoreCase = true) ||
                    name.contains("stETH", ignoreCase = true) ||
                    name.contains("baking", ignoreCase = true) -> TokenAccountCategories.DEFI
            name.contains("NFT", ignoreCase = true) -> TokenAccountCategories.NFTS
            else -> {
                TokenAccountCategories.HOLDINGS
            }
        }
    }
}