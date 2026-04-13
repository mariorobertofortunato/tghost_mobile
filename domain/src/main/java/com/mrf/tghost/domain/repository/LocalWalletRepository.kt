package com.mrf.tghost.domain.repository

import com.mrf.tghost.domain.model.Wallet
import kotlinx.coroutines.flow.Flow

interface LocalWalletRepository {
    fun getWallets(): Flow<List<Wallet>>
    fun getWallet(publicKey: String): Wallet?
    suspend fun addWallet(wallet: Wallet)
    suspend fun deleteWallet(publicKey: String)
    suspend fun updateWallet(wallet: Wallet)
}
