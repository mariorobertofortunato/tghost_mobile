package com.mrf.tghost.domain.usecase.preferences

import com.mrf.tghost.domain.model.RpcPreference
import com.mrf.tghost.domain.model.SupportedChain
import com.mrf.tghost.domain.model.SupportedChainId
import com.mrf.tghost.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRpcPreferenceUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {
    operator fun invoke(chainId: SupportedChainId): Flow<RpcPreference> =
        preferencesRepository.getRpcPreference(chainId)
}
