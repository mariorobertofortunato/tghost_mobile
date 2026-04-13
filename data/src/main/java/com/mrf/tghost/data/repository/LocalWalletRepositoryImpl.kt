package com.mrf.tghost.data.repository

import com.mrf.tghost.data.database.TghostDao
import com.mrf.tghost.data.database.mappers.toEntity
import com.mrf.tghost.data.network.mappers.toDomainModel
import com.mrf.tghost.domain.model.Wallet
import com.mrf.tghost.domain.repository.LocalWalletRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalWalletRepositoryImpl @Inject constructor(
    private val tghostDao: TghostDao
) : LocalWalletRepository {
    override fun getWallets(): Flow<List<Wallet>> = tghostDao.getWallets().map { walletEntities ->
        walletEntities.map { it.toDomainModel() }
    }

    override fun getWallet(publicKey: String): Wallet? = tghostDao.getWallet(publicKey)?.toDomainModel()

    override suspend fun addWallet(wallet: Wallet) = tghostDao.insertNewWallet(wallet.toEntity())

    override suspend fun deleteWallet(publicKey: String) = tghostDao.deleteWallet(publicKey)

    override suspend fun updateWallet(wallet: Wallet) = tghostDao.updateWallet(wallet.toEntity())
}
