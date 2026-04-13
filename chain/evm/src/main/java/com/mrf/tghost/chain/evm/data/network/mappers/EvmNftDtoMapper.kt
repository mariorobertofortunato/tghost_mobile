package com.mrf.tghost.chain.evm.data.network.mappers

import com.mrf.tghost.chain.evm.data.network.model.moralis.EvmNftDto
import com.mrf.tghost.chain.evm.data.network.model.moralis.EvmNftResultDto
import com.mrf.tghost.chain.evm.data.network.model.moralis.EvmNftListPriceDto
import com.mrf.tghost.chain.evm.data.network.model.moralis.EvmNftNormalizedMetadataDto
import com.mrf.tghost.chain.evm.domain.model.EvmNftListPrice as MoralisListPriceDomain
import com.mrf.tghost.chain.evm.domain.model.EvmNftResult as MoralisNftResultDomain
import com.mrf.tghost.chain.evm.domain.model.EvmNftNormalizedMetadata as MoralisNormalizedMetadataDomain
import com.mrf.tghost.chain.evm.domain.model.EvmNftResponse as MoralistNftResponseDomain

fun EvmNftDto.toDomainModel(): MoralistNftResponseDomain {
    return MoralistNftResponseDomain(
        status = status,
        page = page,
        pageSize = pageSize,
        cursor = cursor,
        result = result?.map { it.toDomainModel() }
    )
}

fun EvmNftResultDto.toDomainModel(): MoralisNftResultDomain {
    return MoralisNftResultDomain(
        amount = amount,
        tokenId = tokenId,
        tokenAddress = tokenAddress,
        contractType = contractType,
        ownerOf = ownerOf,
        lastMetadataSync = lastMetadataSync,
        lastTokenUriSync = lastTokenUriSync,
        metadata = metadata,
        blockNumber = blockNumber,
        blockNumberMinted = blockNumberMinted,
        name = name,
        symbol = symbol,
        tokenHash = tokenHash,
        tokenUri = tokenUri,
        minterAddress = minterAddress,
        rarityRank = rarityRank,
        rarityPercentage = rarityPercentage,
        rarityLabel = rarityLabel,
        verifiedCollection = verifiedCollection,
        possibleSpam = possibleSpam,
        normalizedMetadata = normalizedMetadata?.toDomainModel(),
        collectionLogo = collectionLogo,
        collectionBannerImage = collectionBannerImage,
        collectionCategory = collectionCategory,
        projectUrl = projectUrl,
        wikiUrl = wikiUrl,
        discordUrl = discordUrl,
        telegramUrl = telegramUrl,
        twitterUsername = twitterUsername,
        instagramUsername = instagramUsername,
        listPrice = listPrice?.toDomainModel(),
        floorPrice = floorPrice,
        floorPriceUsd = floorPriceUsd,
        floorPriceCurrency = floorPriceCurrency
    )
}

fun EvmNftNormalizedMetadataDto.toDomainModel(): MoralisNormalizedMetadataDomain {
    return MoralisNormalizedMetadataDomain(
        name = name,
        description = description,
        animationUrl = animationUrl,
        externalLink = externalLink,
        externalUrl = externalUrl,
        image = image
    )
}

fun EvmNftListPriceDto.toDomainModel(): MoralisListPriceDomain {
    return MoralisListPriceDomain(
        listed = listed,
        price = price,
        priceCurrency = priceCurrency,
        priceUsd = priceUsd,
        marketplace = marketplace
    )
}
