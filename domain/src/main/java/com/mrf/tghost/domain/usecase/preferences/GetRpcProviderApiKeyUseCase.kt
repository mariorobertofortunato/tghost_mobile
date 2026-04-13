package com.mrf.tghost.domain.usecase.preferences

import com.mrf.tghost.domain.model.RpcProviderId
import com.mrf.tghost.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRpcProviderApiKeyUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {
    operator fun invoke(providerId: RpcProviderId): Flow<String?> =
        preferencesRepository.getRpcProviderApiKey(providerId)
}
