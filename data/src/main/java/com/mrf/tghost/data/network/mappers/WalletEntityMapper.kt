package com.mrf.tghost.data.network.mappers

import com.mrf.tghost.data.database.entities.WalletEntity
import com.mrf.tghost.data.database.entities.WalletEntitySnapshot
import com.mrf.tghost.domain.model.Wallet
import com.mrf.tghost.domain.model.WalletSnapshot

fun List<WalletEntity>.toDomainModel(): List<Wallet> {
    return map { it.toDomainModel() }
}

fun WalletEntity.toDomainModel(): Wallet {
    return Wallet(
        publicKey = publicKey,
        name = name,
        chainId = chainId,
        snapshot = snapshot?.toDomainModel()
    )
}

fun WalletEntitySnapshot.toDomainModel(): WalletSnapshot {
    return WalletSnapshot(
        timestamp = timestamp,
        balanceUSd = balanceUSd,
        balanceNative = balanceNative
    )
}