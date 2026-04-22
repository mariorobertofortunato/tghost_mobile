package com.mrf.tghost.chain.solana.data.network.mappers

import com.mrf.tghost.chain.solana.data.network.model.SolanaStakeAccountDto
import com.mrf.tghost.chain.solana.domain.model.SolanaStake
import java.math.BigDecimal

fun SolanaStakeAccountDto.toSolanaStake(): SolanaStake {
    val activationEpoch = account.data.parsed.info.stake?.delegation?.activationEpoch?.toULong() ?: 0uL
    return SolanaStake(
        pubkey = pubkey,
        validatorAddress = account.data.parsed.info.stake?.delegation?.voter ?: "",
        amount = BigDecimal(account.lamports),
        status = account.data.parsed.type,
        activationEpoch = activationEpoch
    )
}