package com.mrf.tghost.chain.solana.domain.repository

import com.mrf.tghost.chain.solana.domain.model.DasApiResponse
import kotlinx.coroutines.flow.Flow
import com.mrf.tghost.domain.model.Result

interface SolanaNftRepository {
    fun solanaNftAccounts(publicKey: String): Flow<Result<DasApiResponse>?>
}