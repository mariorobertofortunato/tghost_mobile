package com.mrf.tghost.domain.usecase.preferences

import com.mrf.tghost.domain.model.RpcProviderId
import com.mrf.tghost.domain.repository.PreferencesRepository
import javax.inject.Inject

class SaveRpcProviderApiKeyUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {
    suspend operator fun invoke(providerId: RpcProviderId, value: String) {
        preferencesRepository.setRpcProviderApiKey(providerId, value)
    }
}
