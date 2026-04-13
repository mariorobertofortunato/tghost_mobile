package com.mrf.tghost.domain.usecase.localwallets

import com.mrf.tghost.domain.model.Wallet
import com.mrf.tghost.domain.repository.LocalWalletRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWalletsUseCase @Inject constructor(
    private val localWalletRepository: LocalWalletRepository
) {
    operator fun invoke(): Flow<List<Wallet>> {
        return localWalletRepository.getWallets()
    }

    fun getWallet(publicKey: String): Wallet? {
        return localWalletRepository.getWallet(publicKey)
    }
}