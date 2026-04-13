package com.mrf.tghost.data.network.mappers

import com.mrf.tghost.data.network.model.metadata.OffChainAttributeDto
import com.mrf.tghost.data.network.model.metadata.OffChainFileDto
import com.mrf.tghost.data.network.model.metadata.OffChainPropertiesDto
import com.mrf.tghost.data.network.model.metadata.TokenOffChainMetadataDto
import com.mrf.tghost.domain.model.metadata.OffChainAttribute
import com.mrf.tghost.domain.model.metadata.OffChainFile
import com.mrf.tghost.domain.model.metadata.OffChainProperties
import com.mrf.tghost.domain.model.metadata.TokenOffChainMetadata

fun TokenOffChainMetadataDto.toDomainModel(): TokenOffChainMetadata {
    return TokenOffChainMetadata(
        name = name,
        symbol = symbol,
        description = description,
        image = image,
        showName = showName,
        createdOn = createdOn,
        twitter = twitter,
        telegram = telegram,
        website = website,
        attributes = attributes?.map { it.toDomainModel() },
        properties = properties?.toDomainModel()
    )
}

fun OffChainAttributeDto.toDomainModel(): OffChainAttribute {
    return OffChainAttribute(
        trait_type = trait_type,
        value = value
    )
}

fun OffChainPropertiesDto.toDomainModel(): OffChainProperties {
    return OffChainProperties(
        files = files?.map { it.toDomainModel() },
        category = category
    )
}

fun OffChainFileDto.toDomainModel(): OffChainFile {
    return OffChainFile(
        uri = uri,
        type = type
    )
}
