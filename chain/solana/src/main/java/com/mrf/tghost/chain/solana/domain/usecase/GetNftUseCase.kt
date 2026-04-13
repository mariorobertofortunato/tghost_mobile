package com.mrf.tghost.chain.solana.domain.usecase

import com.mrf.tghost.chain.solana.domain.model.DasApiResponse
import com.mrf.tghost.chain.solana.domain.repository.SolanaNftRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import com.mrf.tghost.domain.model.Result

class GetNftUseCase @Inject constructor(
    private val solanaNftRepository: SolanaNftRepository
) {

    fun solanaNftAccounts(publicKey: String): Flow<Result<DasApiResponse>?> =
        solanaNftRepository.solanaNftAccounts(publicKey).onStart { emit(null) }

}
