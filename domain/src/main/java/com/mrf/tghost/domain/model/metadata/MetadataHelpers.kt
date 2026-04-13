package com.mrf.tghost.domain.model.metadata

object MetadataHelpers {

    fun combineMetadata(
        onChainMetadata: TokenOnChainMetadata?,
        offChainMetadata: TokenOffChainMetadata?
    ): TokenCombinedMetadata {
        return TokenCombinedMetadata(
            key = onChainMetadata?.key,
            updateAuthority = onChainMetadata?.updateAuthority,
            mint = onChainMetadata?.mint,
            name = onChainMetadata?.name ?: offChainMetadata?.name,
            symbol = onChainMetadata?.symbol ?: offChainMetadata?.symbol,
            uri = onChainMetadata?.uri,
            sellerFeeBasisPoints = onChainMetadata?.sellerFeeBasisPoints,
            creators = onChainMetadata?.creators,
            primarySaleHappened = onChainMetadata?.primarySaleHappened,
            isMutable = onChainMetadata?.isMutable,
            editionNonce = onChainMetadata?.editionNonce,
            description = offChainMetadata?.description,
            image = offChainMetadata?.image,
            showName = offChainMetadata?.showName,
            createdOn = offChainMetadata?.createdOn,
            twitter = offChainMetadata?.twitter,
            telegram = offChainMetadata?.telegram,
            website = offChainMetadata?.website,
            attributes = offChainMetadata?.attributes,
            properties = offChainMetadata?.properties
        )
    }

}