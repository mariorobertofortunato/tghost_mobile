package com.mrf.tghost.data.database.mappers

import com.mrf.tghost.data.database.entities.WalletEntity
import com.mrf.tghost.data.database.entities.WalletEntitySnapshot
import com.mrf.tghost.domain.model.Wallet
import com.mrf.tghost.domain.model.WalletSnapshot

fun Wallet.toEntity(): WalletEntity {
    return WalletEntity(
        publicKey = publicKey,
        name = name,
        chainId = chainId,
        snapshot = snapshot?.toEntity()
    )
}

fun WalletSnapshot.toEntity(): WalletEntitySnapshot {
    return WalletEntitySnapshot(
        timestamp = timestamp,
        balanceUSd = balanceUSd,
        balanceNative = balanceNative
    )
}
