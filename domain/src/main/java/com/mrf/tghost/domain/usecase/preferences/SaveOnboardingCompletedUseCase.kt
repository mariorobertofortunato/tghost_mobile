package com.mrf.tghost.domain.usecase.preferences

import com.mrf.tghost.domain.repository.PreferencesRepository
import javax.inject.Inject

class SaveOnboardingCompletedUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {
    suspend operator fun invoke(completed: Boolean) {
        preferencesRepository.setOnboardingCompleted(completed)
    }
}
