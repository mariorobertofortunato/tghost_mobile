package com.mrf.tghost.chain.solana.data.network.mappers

import com.mrf.tghost.chain.solana.data.network.model.AccountInfoDto
import com.mrf.tghost.chain.solana.data.network.model.ParsedAccountDataDto
import com.mrf.tghost.chain.solana.data.network.model.ParsedSplTokenDataDto
import com.mrf.tghost.chain.solana.data.network.model.SolanaSplTokenAccountDto
import com.mrf.tghost.chain.solana.data.network.model.SplTokenInfoDto
import com.mrf.tghost.chain.solana.data.network.model.TokenAmountDataDto
import com.mrf.tghost.chain.solana.domain.model.AccountInfo
import com.mrf.tghost.chain.solana.domain.model.ParsedAccountData
import com.mrf.tghost.chain.solana.domain.model.ParsedSplTokenData
import com.mrf.tghost.chain.solana.domain.model.SolanaSplTokenAccount
import com.mrf.tghost.chain.solana.domain.model.SplTokenInfo
import com.mrf.tghost.chain.solana.domain.model.TokenAmountData

fun SolanaSplTokenAccountDto.toDomainModel(): SolanaSplTokenAccount {
    return SolanaSplTokenAccount(
        pubkey = pubkey,
        account = account.toDomainModel()
    )
}

fun AccountInfoDto.toDomainModel(): AccountInfo {
    return AccountInfo(
        lamports = lamports,
        data = data.toDomainModel(),
        owner = owner,
        executable = executable,
        rentEpoch = rentEpoch,
        space = space
    )
}

fun ParsedAccountDataDto.toDomainModel(): ParsedAccountData {
    return ParsedAccountData(
        program = program,
        parsed = parsed.toDomainModel(),
        space = space
    )
}

fun ParsedSplTokenDataDto.toDomainModel(): ParsedSplTokenData {
    return ParsedSplTokenData(
        info = info.toDomainModel(),
        type = type
    )
}

fun SplTokenInfoDto.toDomainModel(): SplTokenInfo {
    return SplTokenInfo(
        isNative = isNative,
        mint = mint,
        owner = owner,
        state = state,
        tokenAmount = tokenAmount.toDomainModel()
    )
}

fun TokenAmountDataDto.toDomainModel(): TokenAmountData {
    return TokenAmountData(
        amount = amount,
        decimals = decimals,
        uiAmount = uiAmount,
        uiAmountString = uiAmountString
    )
}
