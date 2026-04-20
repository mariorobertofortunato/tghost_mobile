package com.mrf.tghost.chain.evm.data.network.mappers

import com.mrf.tghost.chain.evm.data.network.model.moralis.AccountDataDto
import com.mrf.tghost.chain.evm.data.network.model.moralis.EvmStakingProtocolDto
import com.mrf.tghost.chain.evm.data.network.model.moralis.PositionDetailsDto
import com.mrf.tghost.chain.evm.data.network.model.moralis.PositionDto
import com.mrf.tghost.chain.evm.data.network.model.moralis.PositionTokenDto
import com.mrf.tghost.chain.evm.data.network.model.moralis.ProjectedEarningsDto
import com.mrf.tghost.chain.evm.domain.model.AccountData
import com.mrf.tghost.chain.evm.domain.model.EvmStakingProtocol
import com.mrf.tghost.chain.evm.domain.model.Position
import com.mrf.tghost.chain.evm.domain.model.PositionDetails
import com.mrf.tghost.chain.evm.domain.model.PositionToken
import com.mrf.tghost.chain.evm.domain.model.ProjectedEarnings
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.longOrNull

fun List<EvmStakingProtocolDto>.toEvmStakingPositions(): List<EvmStakingProtocol> {
    return this.map { it.toDomainModel() }
}

fun EvmStakingProtocolDto.toDomainModel(): EvmStakingProtocol {
    return EvmStakingProtocol(
        protocolName = protocolName,
        protocolId = protocolId,
        protocolUrl = protocolUrl,
        protocolLogo = protocolLogo,
        accountData = accountData?.toDomainModel(),
        totalProjectedEarningsUsd = totalProjectedEarningsUsd?.toDomainModel(),
        position = position?.toDomainModel()
    )
}

fun AccountDataDto.toDomainModel(): AccountData {
    return AccountData(
        netApy = netApy,
        healthFactor = healthFactor
    )
}

fun ProjectedEarningsDto.toDomainModel(): ProjectedEarnings {
    return ProjectedEarnings(
        daily = daily,
        weekly = weekly,
        monthly = monthly,
        yearly = yearly
    )
}

fun PositionDto.toDomainModel(): Position {
    return Position(
        label = label,
        address = address,
        balanceUsd = balanceUsd.toDoubleLenient(),
        totalUnclaimedUsdValue = totalUnclaimedUsdValue.toDoubleLenient(),
        tokens = tokens?.map { it.toDomainModel() },
        positionDetails = positionDetails?.toDomainModel()
    )
}

fun PositionTokenDto.toDomainModel(): PositionToken {
    return PositionToken(
        tokenType = tokenType,
        name = name,
        symbol = symbol,
        contractAddress = contractAddress,
        decimals = decimals,
        logo = logo,
        thumbnail = thumbnail,
        balance = balance,
        balanceFormatted = balanceFormatted,
        usdPrice = usdPrice.toDoubleLenient(),
        usdValue = usdValue.toDoubleLenient()
    )
}

fun PositionDetailsDto.toDomainModel(): PositionDetails {
    return PositionDetails(
        reserve0 = reserve0,
        reserve1 = reserve1,
        factory = factory,
        pair = pair,
        shareOfPool = shareOfPool,
        market = market,
        isDebt = isDebt,
        isVariableDebt = isVariableDebt,
        isStableDebt = isStableDebt,
        apy = apy,
        projectedEarningsUsd = projectedEarningsUsd?.toDomainModel(),
        isEnabledAsCollateral = isEnabledAsCollateral
    )
}

/** Moralis sometimes returns USD fields as JSON numbers, sometimes as strings — accept both. */
fun JsonElement?.toDoubleLenient(): Double? {
    if (this == null || this === JsonNull) return null
    val prim = this as? JsonPrimitive ?: return null
    prim.doubleOrNull?.let { return it }
    prim.longOrNull?.let { return it.toDouble() }
    return prim.content.toDoubleOrNull()
}
