package com.mrf.tghost.domain.usecase.preferences

import com.mrf.tghost.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLiveUpdateStatusUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {
    operator fun invoke(): Flow<Boolean> {
        return preferencesRepository.getLiveUpdateStatus()
    }
}