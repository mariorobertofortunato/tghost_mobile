package com.mrf.tghost.chain.evm.data.network.mappers

import com.mrf.tghost.chain.evm.data.network.model.alchemy.EvmTokenBalanceDto
import com.mrf.tghost.chain.evm.data.network.model.alchemy.EvmTokenBalancesDto
import com.mrf.tghost.chain.evm.domain.model.EvmTokenBalance
import com.mrf.tghost.chain.evm.domain.model.EvmTokenBalances

fun EvmTokenBalancesDto.toDomainModel(): EvmTokenBalances {
    return EvmTokenBalances(
        address = address,
        tokenBalances = tokenBalances.map { it.toDomainModel() }
    )
}

fun EvmTokenBalanceDto.toDomainModel(): EvmTokenBalance {
    return EvmTokenBalance(
        contractAddress = contractAddress,
        tokenBalance = tokenBalance,
        error = error
    )
}
