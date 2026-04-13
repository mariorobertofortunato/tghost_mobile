package com.mrf.tghost.data.network.mappers

import com.mrf.tghost.data.network.model.LiquidityDto as LiquidityDto
import com.mrf.tghost.data.network.model.MarketDataInfoDto
import com.mrf.tghost.data.network.model.PriceChangeDto as PriceChangeDto
import com.mrf.tghost.data.network.model.SocialDto as SocialDto
import com.mrf.tghost.data.network.model.TokenDto as TokenDto
import com.mrf.tghost.data.network.model.MarketDataTokenInfoDto
import com.mrf.tghost.data.network.model.TransactionPairDto as TransactionPairDto
import com.mrf.tghost.data.network.model.TransactionsDto as TransactionsDto
import com.mrf.tghost.data.network.model.VolumeDto as VolumeDto
import com.mrf.tghost.data.network.model.WebsiteDto as WebsiteDto
import com.mrf.tghost.domain.model.Liquidity
import com.mrf.tghost.domain.model.TokenMarketDataInfo
import com.mrf.tghost.domain.model.PriceChange
import com.mrf.tghost.domain.model.Social
import com.mrf.tghost.domain.model.Token
import com.mrf.tghost.domain.model.TokenMarketData
import com.mrf.tghost.domain.model.TransactionPair
import com.mrf.tghost.domain.model.Transactions
import com.mrf.tghost.domain.model.Volume
import com.mrf.tghost.domain.model.Website

fun MarketDataInfoDto.toDomainModel(): TokenMarketDataInfo {
    return TokenMarketDataInfo(
        chainId = chainId,
        dexId = dexId,
        url = url,
        pairAddress = pairAddress,
        labels = labels,
        baseToken = baseToken?.toDomainModel(),
        quoteToken = quoteToken?.toDomainModel(),
        priceNative = priceNative,
        priceUsd = priceUsd,
        txns = txns?.toDomainModel(),
        volume = volume?.toDomainModel(),
        priceChange = priceChange?.toDomainModel(),
        liquidity = liquidity?.toDomainModel(),
        fdv = fdv,
        marketCap = marketCap,
        pairCreatedAt = pairCreatedAt,
        info = info?.toDomainModel()
    )
}

fun TokenDto.toDomainModel(): Token {
    return Token(
        address = address,
        name = name,
        symbol = symbol
    )
}

fun TransactionsDto.toDomainModel(): Transactions {
    return Transactions(
        m5 = m5?.toDomainModel(),
        h1 = h1?.toDomainModel(),
        h6 = h6?.toDomainModel(),
        h24 = h24?.toDomainModel()
    )
}

fun TransactionPairDto.toDomainModel(): TransactionPair {
    return TransactionPair(
        buys = buys,
        sells = sells
    )
}

fun VolumeDto.toDomainModel(): Volume {
    return Volume(
        h24 = h24,
        h6 = h6,
        h1 = h1,
        m5 = m5
    )
}

fun PriceChangeDto.toDomainModel(): PriceChange {
    return PriceChange(
        m5 = m5,
        h1 = h1,
        h6 = h6,
        h24 = h24
    )
}

fun LiquidityDto.toDomainModel(): Liquidity {
    return Liquidity(
        usd = usd,
        base = base,
        quote = quote
    )
}

fun SocialDto.toDomainModel(): Social {
    return Social(
        type = type,
        url = url
    )
}

fun MarketDataTokenInfoDto.toDomainModel(): TokenMarketData {
    return TokenMarketData(
        imageUrl = imageUrl,
        header = header,
        openGraph = openGraph,
        websites = websites.map { it.toDomainModel() },
        socials = socials.map { it.toDomainModel() }
    )
}

fun WebsiteDto.toDomainModel(): Website {
    return Website(
        label = label,
        url = url
    )
}
