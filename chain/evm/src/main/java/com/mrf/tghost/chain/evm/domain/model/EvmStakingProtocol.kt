package com.mrf.tghost.chain.evm.domain.model

//typealias DefiPositionsResponse = List<EvmStakingProtocol>

data class EvmStakingProtocol(
    val protocolName: String? = null,
    val protocolId: String? = null,
    val protocolUrl: String? = null,
    val protocolLogo: String? = null,
    val accountData: AccountData? = null,
    val totalProjectedEarningsUsd: ProjectedEarnings? = null,
    val position: Position? = null
)

data class AccountData(
    val netApy: Double? = null,
    val healthFactor: Double? = null
)

data class ProjectedEarnings(
    val daily: Double? = null,
    val weekly: Double? = null,
    val monthly: Double? = null,
    val yearly: Double? = null
)

data class Position(
    val label: String? = null,
    val address: String? = null,
    val balanceUsd: Double? = null,
    val totalUnclaimedUsdValue: Double? = null,
    val tokens: List<PositionToken>? = null,
    val positionDetails: PositionDetails? = null
)

data class PositionToken(
    val tokenType: String? = null,
    val name: String? = null,
    val symbol: String? = null,
    val contractAddress: String? = null,
    val decimals: String? = null,
    val logo: String? = null,
    val thumbnail: String? = null,
    val balance: String? = null,
    val balanceFormatted: String? = null,
    val usdPrice: Double? = null,
    val usdValue: Double? = null
)

data class PositionDetails(
    val reserve0: String? = null,
    val reserve1: String? = null,
    val factory: String? = null,
    val pair: String? = null,
    val shareOfPool: Double? = null,
    // Aave-specific
    val market: String? = null,
    val isDebt: Boolean? = null,
    val isVariableDebt: Boolean? = null,
    val isStableDebt: Boolean? = null,
    val apy: Double? = null,
    val projectedEarningsUsd: ProjectedEarnings? = null,
    val isEnabledAsCollateral: Boolean? = null
)
