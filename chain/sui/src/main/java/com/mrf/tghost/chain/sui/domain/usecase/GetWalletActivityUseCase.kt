package com.mrf.tghost.chain.sui.domain.usecase

import com.mrf.tghost.chain.sui.domain.repository.SuiWalletActivityRepository
import com.mrf.tghost.domain.model.Result
import com.mrf.tghost.domain.model.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class GetWalletActivityUseCase @Inject constructor(
    private val walletActivityRepository: SuiWalletActivityRepository
) {
    fun suiWalletActivity(walletAddress: String): Flow<Result<List<Transaction>>?> =
        walletActivityRepository.suiWalletActivity(walletAddress).onStart { emit(null) }
}
