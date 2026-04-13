package com.mrf.tghost.domain.usecase.preferences

import com.mrf.tghost.domain.repository.PreferencesRepository
import javax.inject.Inject

class SaveLiveUpdateStatus @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {
    suspend operator fun invoke(enabled: Boolean) {
        preferencesRepository.setLiveUpdateStatus(enabled)
    }
}
