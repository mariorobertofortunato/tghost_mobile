package com.mrf.tghost.chain.sui.domain.model

import kotlinx.serialization.json.JsonObject

data class SuiCoin(
    val coinType: String,
    val coinObjectCount: Int,
    val totalBalance: String,
    val lockedBalance: JsonObject? = null
)