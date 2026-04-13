package com.mrf.tghost.chain.sui.data.network.mappers

import com.mrf.tghost.chain.sui.data.network.model.SuiCoinMetadataGraphQlDto
import com.mrf.tghost.chain.sui.domain.model.SuiCoinMetadata

fun SuiCoinMetadataGraphQlDto.toDomainModel(): SuiCoinMetadata {
    return SuiCoinMetadata(
        decimals = decimals,
        name = name,
        symbol = symbol,
        description = description.orEmpty(),
        iconUrl = iconUrl?.takeIf { it.isNotBlank() },
        id = address
    )
}
