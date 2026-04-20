package com.mrf.tghost.chain.evm.data.network.model.moralis

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNames

@Serializable
data class EvmStakingProtocolDto(
    @SerialName("protocol_name")                    val protocolName: String? = null,
    @SerialName("protocol_id")                      val protocolId: String? = null,
    @SerialName("protocol_url")                     val protocolUrl: String? = null,
    @SerialName("protocol_logo")                    val protocolLogo: String? = null,
    @SerialName("account_data")                     val accountData: AccountDataDto? = null,
    @SerialName("total_projected_earnings_usd")     val totalProjectedEarningsUsd: ProjectedEarningsDto? = null,
    @SerialName("position")                         val position: PositionDto? = null
)

@Serializable
data class AccountDataDto(
    @SerialName("net_apy")          val netApy: Double? = null,
    @SerialName("health_factor")    val healthFactor: Double? = null
)

@Serializable
data class ProjectedEarningsDto(
    @SerialName("daily")    val daily: Double? = null,
    @SerialName("weekly")   val weekly: Double? = null,
    @SerialName("monthly")  val monthly: Double? = null,
    @SerialName("yearly")   val yearly: Double? = null
)

@Serializable
data class PositionDto(
    @SerialName("label")                        val label: String? = null,
    @SerialName("address")                      val address: String? = null,
    @SerialName("balance_usd")                  val balanceUsd: JsonElement? = null,
    @SerialName("total_unclaimed_usd_value")    val totalUnclaimedUsdValue: JsonElement? = null,
    @SerialName("tokens")                       val tokens: List<PositionTokenDto>? = null,
    @SerialName("position_details")             val positionDetails: PositionDetailsDto? = null
)

@Serializable
data class PositionTokenDto(
    @SerialName("token_type")           val tokenType: String? = null,
    @SerialName("name")                 val name: String? = null,
    @SerialName("symbol")               val symbol: String? = null,
    @SerialName("contract_address")     val contractAddress: String? = null,
    @SerialName("decimals")             val decimals: String? = null,
    @SerialName("logo")                 val logo: String? = null,
    @SerialName("thumbnail")            val thumbnail: String? = null,
    @SerialName("balance")              val balance: String? = null,
    @SerialName("balance_formatted")    val balanceFormatted: String? = null,
    @SerialName("usd_price")            val usdPrice: JsonElement? = null,
    @SerialName("usd_value")            val usdValue: JsonElement? = null
)

@Serializable
data class PositionDetailsDto(
    @SerialName("reserve0")                 val reserve0: String? = null,
    @SerialName("reserve1")                 val reserve1: String? = null,
    @SerialName("factory")                  val factory: String? = null,
    @SerialName("pair")                     val pair: String? = null,
    @SerialName("share_of_pool")            val shareOfPool: Double? = null,
    // Aave-specific
    @SerialName("market")                   val market: String? = null,
    @SerialName("is_debt")                  val isDebt: Boolean? = null,
    @SerialName("is_variable_debt")         val isVariableDebt: Boolean? = null,
    @SerialName("is_stable_debt")           val isStableDebt: Boolean? = null,
    @SerialName("apy")                      val apy: Double? = null,
    @SerialName("projected_earnings_usd")   val projectedEarningsUsd: ProjectedEarningsDto? = null,
    @SerialName("is_enabled_as_collateral")
    @JsonNames("is_enabled_collateral")
    val isEnabledAsCollateral: Boolean? = null
)