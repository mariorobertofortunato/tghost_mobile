package com.mrf.tghost.chain.evm.data.network.mappers

import com.mrf.tghost.chain.evm.data.network.model.alchemy.AlchemyNftAttributeDto
import com.mrf.tghost.chain.evm.data.network.model.alchemy.AlchemyNftsByAddressResponseDto
import com.mrf.tghost.chain.evm.data.network.model.alchemy.AlchemyOwnedNftDto
import com.mrf.tghost.chain.evm.domain.model.EvmNftAttribute
import com.mrf.tghost.chain.evm.domain.model.EvmNftNormalizedMetadata
import com.mrf.tghost.chain.evm.domain.model.EvmNftResponse
import com.mrf.tghost.chain.evm.domain.model.EvmNftResult

fun AlchemyNftsByAddressResponseDto.toEvmNftResponse(): EvmNftResponse {
    val data = this.data
    return EvmNftResponse(
        status = null,
        page = null,
        pageSize = null,
        cursor = data?.pageKey,
        totalCount = data?.totalCount,
        result = data?.ownedNfts?.map { it.toEvmNftResult() },
    )
}

/** Alchemy often puts only the mint index here (e.g. `#1`); prefer collection/contract name when so. */
private val alchemyTokenIndexOnlyName = Regex("^#\\d+$")

private fun AlchemyOwnedNftDto.resolveDisplayName(): String? {
    val tokenLabel = name ?: raw?.metadata?.name
    val collectionOrContractName = contract?.name
        ?: contract?.openSeaMetadata?.collectionName
        ?: collection?.name
    return if (tokenLabel != null && tokenLabel.matches(alchemyTokenIndexOnlyName)) {
        collectionOrContractName ?: tokenLabel
    } else {
        tokenLabel ?: collectionOrContractName
    }
}

private fun AlchemyOwnedNftDto.toEvmNftResult(): EvmNftResult {
    val contractAddr = contract?.address.orEmpty()
    val imageUrl = image?.cachedUrl
        ?: image?.pngUrl
        ?: image?.thumbnailUrl
        ?: image?.originalUrl
        ?: raw?.metadata?.image
        ?: contract?.openSeaMetadata?.imageUrl

    val metaName = resolveDisplayName()
    val metaDesc = description ?: raw?.metadata?.description ?: contract?.openSeaMetadata?.description
    val externalUrl = collection?.externalUrl
        ?: contract?.openSeaMetadata?.externalUrl
        ?: raw?.metadata?.externalUrl
    val spam = contract?.isSpam == true

    val floor = contract?.openSeaMetadata?.floorPrice?.toString()

    return EvmNftResult(
        amount = balance ?: "1",
        tokenId = tokenId,
        tokenAddress = contractAddr,
        contractType = tokenType ?: contract?.tokenType,
        ownerOf = address,
        network = network,
        lastMetadataSync = timeLastUpdated,
        lastTokenUriSync = null,
        metadata = null,
        blockNumber = acquiredAt?.blockNumber?.toString(),
        blockNumberMinted = null,
        name = metaName,
        symbol = contract?.symbol,
        tokenHash = null,
        tokenUri = tokenUri ?: raw?.tokenUri,
        minterAddress = null,
        rarityRank = null,
        rarityPercentage = null,
        rarityLabel = null,
        verifiedCollection = null,
        possibleSpam = spam,
        normalizedMetadata = EvmNftNormalizedMetadata(
            name = metaName,
            description = metaDesc,
            animationUrl = raw?.metadata?.animationUrl,
            externalLink = externalUrl,
            externalUrl = externalUrl,
            image = imageUrl,
            attributes = raw?.metadata?.attributes?.map { it.toDomain() }.orEmpty(),
        ),
        collectionLogo = contract?.openSeaMetadata?.imageUrl,
        collectionBannerImage = collection?.bannerImageUrl ?: contract?.openSeaMetadata?.bannerImageUrl,
        collectionCategory = null,
        projectUrl = null,
        wikiUrl = null,
        discordUrl = contract?.openSeaMetadata?.discordUrl,
        telegramUrl = null,
        twitterUsername = contract?.openSeaMetadata?.twitterUsername,
        instagramUsername = null,
        listPrice = null,
        floorPrice = floor,
        floorPriceUsd = null,
        floorPriceCurrency = null,
        lastSale = null,
        media = null,
    )
}

private fun AlchemyNftAttributeDto.toDomain(): EvmNftAttribute =
    EvmNftAttribute(
        traitType = traitType,
        value = value,
        displayType = null,
        maxValue = null,
        traitCount = null,
        order = null,
    )
