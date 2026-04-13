package com.mrf.tghost.chain.tezos.data.network.mappers

import com.mrf.tghost.chain.tezos.data.network.model.TezosContractInfoDto
import com.mrf.tghost.chain.tezos.data.network.model.TezosTokenDto
import com.mrf.tghost.chain.tezos.data.network.model.TezosTokenInfoDto
import com.mrf.tghost.chain.tezos.data.network.model.TezosTokenMetadataDto
import com.mrf.tghost.chain.tezos.domain.model.TezosContractInfo
import com.mrf.tghost.chain.tezos.domain.model.TezosToken
import com.mrf.tghost.chain.tezos.domain.model.TezosTokenInfo
import com.mrf.tghost.chain.tezos.domain.model.TezosTokenMetadata

fun TezosTokenDto.toDomainModel(): TezosToken {
    return TezosToken(
        token = token?.toDomainModel(),
        balance = balance
    )
}

fun TezosTokenInfoDto.toDomainModel(): TezosTokenInfo {
    return TezosTokenInfo(
        contract = contract.toDomainModel(),
        metadata = metadata?.toDomainModel(),
        tokenId = tokenId,
        standard = standard,
        totalSupply = totalSupply
    )
}

fun TezosContractInfoDto.toDomainModel(): TezosContractInfo {
    return TezosContractInfo(
        address = address
    )
}

fun TezosTokenMetadataDto.toDomainModel(): TezosTokenMetadata {
    return TezosTokenMetadata(
        name = name,
        symbol = symbol,
        decimals = decimals,
        description = description,
        displayUri = displayUri,
        thumbnailUri = thumbnailUri,
        artifactUri = artifactUri
    )
}