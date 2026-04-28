package com.mrf.tghost.chain.sui.domain.model

data class SuiOwnedObjectsSplit(
    val coinObjects: List<SuiCoin>,
    val nftObjects: List<SuiObject>,
    val stakedSuiObjects: List<SuiObject>
)
