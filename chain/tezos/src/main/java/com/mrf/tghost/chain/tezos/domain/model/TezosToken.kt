package com.mrf.tghost.chain.tezos.domain.model

data class TezosToken(
    val token: TezosTokenInfo?,
    val balance: String
)

data class TezosTokenInfo(
    val contract: TezosContractInfo,
    val metadata: TezosTokenMetadata? = null,
    val tokenId: String? = null,
    val standard: String? = null,
    val totalSupply: String? = null
)

data class TezosContractInfo(
    val address: String
)


data class TezosTokenMetadata(
    val name: String? = null,
    val symbol: String? = null,
    val decimals: String? = "0",
    val description: String? = null,
    val displayUri: String? = null,
    val thumbnailUri: String? = null,
    val artifactUri: String? = null
)
