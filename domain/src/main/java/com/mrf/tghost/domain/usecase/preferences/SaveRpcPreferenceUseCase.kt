package com.mrf.tghost.domain.usecase.preferences

import com.mrf.tghost.domain.model.RpcPreference
import com.mrf.tghost.domain.model.SupportedChain
import com.mrf.tghost.domain.model.SupportedChainId
import com.mrf.tghost.domain.repository.PreferencesRepository
import javax.inject.Inject

class SaveRpcPreferenceUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {
    suspend operator fun invoke(chainId: SupportedChainId, preference: RpcPreference) {
        preferencesRepository.setRpcPreference(chainId, preference)
    }
}
