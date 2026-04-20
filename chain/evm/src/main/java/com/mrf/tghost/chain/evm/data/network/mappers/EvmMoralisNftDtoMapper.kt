package com.mrf.tghost.chain.evm.data.network.mappers

import com.mrf.tghost.chain.evm.data.network.model.moralis.MoralisEvmNftAttributeDto
import com.mrf.tghost.chain.evm.data.network.model.moralis.MoralisEvmNftDto
import com.mrf.tghost.chain.evm.data.network.model.moralis.MoralisEvmNftLastSaleDto
import com.mrf.tghost.chain.evm.data.network.model.moralis.MoralisEvmNftListPriceDto
import com.mrf.tghost.chain.evm.data.network.model.moralis.MoralisEvmNftMediaDto
import com.mrf.tghost.chain.evm.data.network.model.moralis.MoralisEvmNftMediaVariantDto
import com.mrf.tghost.chain.evm.data.network.model.moralis.MoralisEvmNftNormalizedMetadataDto
import com.mrf.tghost.chain.evm.data.network.model.moralis.MoralisEvmNftPaymentTokenDto
import com.mrf.tghost.chain.evm.data.network.model.moralis.MoralisEvmNftResultDto
import com.mrf.tghost.chain.evm.domain.model.EvmNftAttribute
import com.mrf.tghost.chain.evm.domain.model.EvmNftLastSale
import com.mrf.tghost.chain.evm.domain.model.EvmNftListPrice as MoralisListPriceDomain
import com.mrf.tghost.chain.evm.domain.model.EvmNftMedia
import com.mrf.tghost.chain.evm.domain.model.EvmNftMediaVariant
import com.mrf.tghost.chain.evm.domain.model.EvmNftPaymentToken
import com.mrf.tghost.chain.evm.domain.model.EvmNftResult as MoralisNftResultDomain
import com.mrf.tghost.chain.evm.domain.model.EvmNftNormalizedMetadata as MoralisNormalizedMetadataDomain
import com.mrf.tghost.chain.evm.domain.model.EvmNftResponse as MoralistNftResponseDomain

fun MoralisEvmNftDto.toDomainModel(): MoralistNftResponseDomain {
    return MoralistNftResponseDomain(
        status = status,
        page = page?.toIntOrNull(),
        pageSize = pageSize?.toIntOrNull(),
        cursor = cursor,
        totalCount = null,
        result = result?.map { it.toDomainModel() },
    )
}

fun MoralisEvmNftResultDto.toDomainModel(): MoralisNftResultDomain {
    return MoralisNftResultDomain(
        amount = amount,
        tokenId = tokenId,
        tokenAddress = tokenAddress,
        contractType = contractType,
        ownerOf = ownerOf,
        network = null,
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
        verifiedCollection = verifiedCollection.toBooleanLenient(),
        possibleSpam = possibleSpam.toBooleanLenient(),
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
        listPrice = listPrice?.toDomainModel() ?: lastSale?.toListPriceFromLastSale(),
        floorPrice = floorPrice,
        floorPriceUsd = floorPriceUsd,
        floorPriceCurrency = floorPriceCurrency,
        lastSale = lastSale?.toDomainModel(),
        media = media?.toDomainModel(),
    )
}

private fun String?.toBooleanLenient(): Boolean? = when (this?.trim()?.lowercase()) {
    "true", "1", "yes" -> true
    "false", "0", "no" -> false
    else -> null
}

private fun MoralisEvmNftLastSaleDto.toListPriceFromLastSale(): MoralisListPriceDomain =
    MoralisListPriceDomain(
        listed = true,
        price = priceFormatted ?: price,
        priceCurrency = paymentToken?.tokenSymbol,
        priceUsd = usdPriceAtSale ?: currentUsdValue,
        marketplace = null,
    )

private fun MoralisEvmNftLastSaleDto.toDomainModel(): EvmNftLastSale =
    EvmNftLastSale(
        transactionHash = transactionHash,
        blockTimestamp = blockTimestamp,
        buyerAddress = buyerAddress,
        sellerAddress = sellerAddress,
        price = price,
        priceFormatted = priceFormatted,
        usdPriceAtSale = usdPriceAtSale,
        currentUsdValue = currentUsdValue,
        tokenAddress = tokenAddress,
        tokenId = tokenId,
        paymentToken = paymentToken?.toDomainModel(),
    )

private fun MoralisEvmNftPaymentTokenDto.toDomainModel(): EvmNftPaymentToken =
    EvmNftPaymentToken(
        tokenName = tokenName,
        tokenSymbol = tokenSymbol,
        tokenLogo = tokenLogo,
        tokenDecimals = tokenDecimals,
        tokenAddress = tokenAddress,
    )

private fun MoralisEvmNftMediaDto.toDomainModel(): EvmNftMedia =
    EvmNftMedia(
        mimetype = mimetype,
        category = category,
        status = status,
        originalMediaUrl = originalMediaUrl,
        updatedAt = updatedAt,
        parentHash = parentHash,
        low = mediaCollection?.low?.toDomainModel(),
        medium = mediaCollection?.medium?.toDomainModel(),
        high = mediaCollection?.high?.toDomainModel(),
    )

private fun MoralisEvmNftMediaVariantDto.toDomainModel(): EvmNftMediaVariant =
    EvmNftMediaVariant(width = width, height = height, url = url)

fun MoralisEvmNftNormalizedMetadataDto.toDomainModel(): MoralisNormalizedMetadataDomain {
    return MoralisNormalizedMetadataDomain(
        name = name,
        description = description,
        animationUrl = animationUrl,
        externalLink = externalLink,
        externalUrl = externalUrl,
        image = image,
        attributes = attributes.map { it.toDomainModel() },
    )
}

private fun MoralisEvmNftAttributeDto.toDomainModel(): EvmNftAttribute =
    EvmNftAttribute(
        traitType = traitType,
        value = value,
        displayType = displayType,
        maxValue = maxValue,
        traitCount = traitCount,
        order = order,
    )

fun MoralisEvmNftListPriceDto.toDomainModel(): MoralisListPriceDomain {
    return MoralisListPriceDomain(
        listed = listed,
        price = price,
        priceCurrency = priceCurrency,
        priceUsd = priceUsd,
        marketplace = marketplace,
    )
}
