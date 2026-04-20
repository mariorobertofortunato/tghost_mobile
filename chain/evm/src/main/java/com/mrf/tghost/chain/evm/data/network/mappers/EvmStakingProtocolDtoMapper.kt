package com.mrf.tghost.chain.evm.data.network.mappers

import com.mrf.tghost.chain.evm.data.network.model.AccountDataDto
import com.mrf.tghost.chain.evm.data.network.model.toDoubleLenient
import com.mrf.tghost.chain.evm.data.network.model.EvmStakingProtocolDto
import com.mrf.tghost.chain.evm.data.network.model.PositionDetailsDto
import com.mrf.tghost.chain.evm.data.network.model.PositionDto
import com.mrf.tghost.chain.evm.data.network.model.PositionTokenDto
import com.mrf.tghost.chain.evm.data.network.model.ProjectedEarningsDto
import com.mrf.tghost.chain.evm.domain.model.AccountData
import com.mrf.tghost.chain.evm.domain.model.EvmStakingProtocol
import com.mrf.tghost.chain.evm.domain.model.Position
import com.mrf.tghost.chain.evm.domain.model.PositionDetails
import com.mrf.tghost.chain.evm.domain.model.PositionToken
import com.mrf.tghost.chain.evm.domain.model.ProjectedEarnings

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
