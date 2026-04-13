package com.mrf.tghost.chain.tezos.domain.model

data class TezosDelegation(
    val type: String,
    val id: Long,
    val level: Int,
    val timestamp: String,
    val block: String,
    val hash: String,
    val delegator: TezosDelegator,
    val prevDelegate: TezosDelegate?,
    val newDelegate: TezosDelegate?,
    val amount: Long,
    val status: String,
)


data class TezosDelegator(
    val address: String,
    val alias: String? = null
)


data class TezosDelegate(
    val address: String,
    val alias: String? = null
)
