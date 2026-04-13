package com.mrf.tghost.chain.evm.data.network.mappers

import com.mrf.tghost.chain.evm.data.network.model.alchemy.EvmMetadataResponseDto
import com.mrf.tghost.chain.evm.domain.model.EvmMetadataResponse

fun EvmMetadataResponseDto.toDomainModel(): EvmMetadataResponse {
    return EvmMetadataResponse(
        name = name,
        symbol = symbol,
        decimals = decimals,
        logo = logo
    )
}
