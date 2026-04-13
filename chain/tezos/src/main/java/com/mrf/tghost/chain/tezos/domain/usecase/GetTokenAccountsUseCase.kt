package com.mrf.tghost.chain.tezos.domain.usecase

import com.mrf.tghost.chain.tezos.domain.model.TezosToken
import com.mrf.tghost.chain.tezos.domain.repository.TezosTokenAccountsRepository
import com.mrf.tghost.domain.model.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class GetTokenAccountsUseCase @Inject constructor(
    private val tokenAccountsRepository: TezosTokenAccountsRepository
) {

    fun tezosTokenAccounts(publicKey: String): Flow<Result<List<TezosToken>>?> =
        tokenAccountsRepository.tezosTokenAccounts(publicKey).onStart { emit(null) }

}