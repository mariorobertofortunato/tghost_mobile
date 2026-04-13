package com.mrf.tghost.chain.evm.data.network.mappers

import com.mrf.tghost.chain.evm.data.network.model.alchemy.EvmTokenAccountDetailsDto
import com.mrf.tghost.chain.evm.domain.model.EvmTokenAccount
import java.math.BigDecimal

fun EvmTokenAccountDetailsDto.toDomainModel(): EvmTokenAccount {
    return EvmTokenAccount(
        contractAddress = contractAddress,
        name = name,
        symbol = symbol,
        decimals = decimals,
        balance = balance?.toBigDecimal() ?: BigDecimal.ZERO,
        rawBalance = balance ?: "0"
    )
}
