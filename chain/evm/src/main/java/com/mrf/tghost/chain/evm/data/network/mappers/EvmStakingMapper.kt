package com.mrf.tghost.chain.evm.data.network.mappers

import com.mrf.tghost.chain.evm.data.network.model.EvmStakingProtocolDto
import com.mrf.tghost.chain.evm.domain.model.EvmStakingProtocol

fun List<EvmStakingProtocolDto>.toEvmStakingPositions(): List<EvmStakingProtocol> {
    return this.map { it.toDomainModel() }
}