package com.mrf.tghost.chain.solana.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SolanaStakeAccountDto(
    @SerialName("pubkey") val pubkey: String,
    @SerialName("account") val account: SolanaStakeAccountInfoDto
)

@Serializable
data class SolanaStakeAccountInfoDto(
    @SerialName("lamports") val lamports: Long,
    @SerialName("data") val data: SolanaParsedStakeDataDto,
    @SerialName("owner") val owner: String,
    @SerialName("executable") val executable: Boolean,
    @SerialName("rentEpoch") val rentEpoch: ULong
)

@Serializable
data class SolanaParsedStakeDataDto(
    @SerialName("program") val program: String,
    @SerialName("parsed") val parsed: SolanaParsedStakeInfoDto
)

@Serializable
data class SolanaParsedStakeInfoDto(
    @SerialName("type") val type: String,
    @SerialName("info") val info: SolanaStakeInfoDetailsDto
)

@Serializable
data class SolanaStakeInfoDetailsDto(
    @SerialName("meta") val meta: SolanaStakeMetaDto,
    @SerialName("stake") val stake: SolanaStakeDelegationInfoDto? = null
)

@Serializable
data class SolanaStakeMetaDto(
    @SerialName("rentExemptReserve") val rentExemptReserve: String,
    @SerialName("authorized") val authorized: SolanaStakeAuthorizedDto
)

@Serializable
data class SolanaStakeAuthorizedDto(
    @SerialName("staker") val staker: String,
    @SerialName("withdrawer") val withdrawer: String
)

@Serializable
data class SolanaStakeDelegationInfoDto(
    @SerialName("delegation") val delegation: SolanaStakeDelegationDto
)

@Serializable
data class SolanaStakeDelegationDto(
    @SerialName("voter") val voter: String,
    @SerialName("stake") val stake: String,
    @SerialName("activationEpoch") val activationEpoch: String,
    @SerialName("deactivationEpoch") val deactivationEpoch: String,
    @SerialName("warmupCooldownRate") val warmupCooldownRate: Double? = null
)
