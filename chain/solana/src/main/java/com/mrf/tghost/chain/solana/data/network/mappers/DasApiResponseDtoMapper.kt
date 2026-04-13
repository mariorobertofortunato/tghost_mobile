package com.mrf.tghost.chain.solana.data.network.mappers

import com.mrf.tghost.chain.solana.data.network.model.DasApiResponseDto
import com.mrf.tghost.chain.solana.data.network.model.DasAuthorityDto
import com.mrf.tghost.chain.solana.data.network.model.DasCompressionDto
import com.mrf.tghost.chain.solana.data.network.model.DasContentDto
import com.mrf.tghost.chain.solana.data.network.model.DasCreatorDto
import com.mrf.tghost.chain.solana.data.network.model.DasFileDto
import com.mrf.tghost.chain.solana.data.network.model.DasGroupingDto
import com.mrf.tghost.chain.solana.data.network.model.DasItemDto
import com.mrf.tghost.chain.solana.data.network.model.DasMetadataDto
import com.mrf.tghost.chain.solana.data.network.model.DasOwnershipDto
import com.mrf.tghost.chain.solana.data.network.model.DasResultDto
import com.mrf.tghost.chain.solana.data.network.model.DasRoyaltyDto
import com.mrf.tghost.chain.solana.data.network.model.DasSupplyDto
import com.mrf.tghost.chain.solana.domain.model.DasApiResponse
import com.mrf.tghost.chain.solana.domain.model.DasAuthority
import com.mrf.tghost.chain.solana.domain.model.DasCompression
import com.mrf.tghost.chain.solana.domain.model.DasContent
import com.mrf.tghost.chain.solana.domain.model.DasCreator
import com.mrf.tghost.chain.solana.domain.model.DasFile
import com.mrf.tghost.chain.solana.domain.model.DasGrouping
import com.mrf.tghost.chain.solana.domain.model.DasItem
import com.mrf.tghost.chain.solana.domain.model.DasMetadata
import com.mrf.tghost.chain.solana.domain.model.DasOwnership
import com.mrf.tghost.chain.solana.domain.model.DasResult
import com.mrf.tghost.chain.solana.domain.model.DasRoyalty
import com.mrf.tghost.chain.solana.domain.model.DasSupply

fun DasApiResponseDto.toDomainModel(): DasApiResponse {
    return DasApiResponse(
        jsonrpc = jsonrpc,
        result = result?.toDomainModel(),
        id = id
    )
}

fun DasResultDto.toDomainModel(): DasResult {
    return DasResult(
        lastIndexedSlot = lastIndexedSlot,
        total = total,
        limit = limit,
        page = page,
        items = items?.map { it.toDomainModel() }
    )
}

fun DasItemDto.toDomainModel(): DasItem {
    return DasItem(
        interfaceName = interfaceName,
        id = id,
        content = content?.toDomainModel(),
        authorities = authorities?.map { it.toDomainModel() },
        compression = compression?.toDomainModel(),
        grouping = grouping?.map { it.toDomainModel() },
        royalty = royalty?.toDomainModel(),
        creators = creators?.map { it.toDomainModel() },
        ownership = ownership?.toDomainModel(),
        supply = supply?.toDomainModel(),
        mutable = mutable,
        burnt = burnt
    )
}

fun DasContentDto.toDomainModel(): DasContent {
    return DasContent(
        schema = schema,
        jsonUri = jsonUri,
        files = files?.map { it.toDomainModel() },
        metadata = metadata?.toDomainModel()
    )
}

fun DasFileDto.toDomainModel(): DasFile {
    return DasFile(
        uri = uri,
        cdnUri = cdnUri,
        mime = mime
    )
}

fun DasAuthorityDto.toDomainModel(): DasAuthority {
    return DasAuthority(
        address = address,
        scopes = scopes
    )
}

fun DasCompressionDto.toDomainModel(): DasCompression {
    return DasCompression(
        eligible = eligible,
        compressed = compressed,
        dataHash = dataHash,
        creatorHash = creatorHash,
        assetHash = assetHash,
        tree = tree,
        seq = seq,
        leafId = leafId
    )
}

fun DasRoyaltyDto.toDomainModel(): DasRoyalty {
    return DasRoyalty(
        royaltyModel = royaltyModel,
        target = target,
        percent = percent,
        basisPoints = basisPoints,
        primarySaleHappened = primarySaleHappened,
        locked = locked
    )
}

fun DasMetadataDto.toDomainModel(): DasMetadata {
    return DasMetadata(
        name = name,
        symbol = symbol,
        description = description,
        creators = creators?.map { it.toDomainModel() }
    )
}

fun DasCreatorDto.toDomainModel(): DasCreator {
    return DasCreator(
        address = address,
        verified = verified,
        share = share
    )
}

fun DasOwnershipDto.toDomainModel(): DasOwnership {
    return DasOwnership(
        frozen = frozen,
        delegated = delegated,
        delegate = delegate,
        ownershipModel = ownershipModel,
        owner = owner
    )
}

fun DasGroupingDto.toDomainModel(): DasGrouping {
    return DasGrouping(
        groupKey = groupKey,
        groupValue = groupValue
    )
}

fun DasSupplyDto.toDomainModel(): DasSupply {
    return DasSupply(
        printMaxSupply = printMaxSupply,
        printCurrentSupply = printCurrentSupply,
        editionNonce = editionNonce
    )
}
