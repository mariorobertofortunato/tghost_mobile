package com.mrf.tghost.domain.usecase.localwallets

import com.mrf.tghost.domain.repository.LocalWalletRepository
import javax.inject.Inject

class DeleteWalletUseCase @Inject constructor(
    private val localWalletRepository: LocalWalletRepository
) {
    suspend operator fun invoke(publicKey: String) {
        localWalletRepository.deleteWallet(publicKey)
    }
}