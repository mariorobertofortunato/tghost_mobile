package com.mrf.tghost.domain.usecase.localwallets

import com.mrf.tghost.domain.model.Wallet
import com.mrf.tghost.domain.repository.LocalWalletRepository
import javax.inject.Inject

class AddWalletUseCase @Inject constructor(
    private val localWalletRepository: LocalWalletRepository
) {
    suspend operator fun invoke(wallet: Wallet) {
        localWalletRepository.addWallet(wallet)
    }
}