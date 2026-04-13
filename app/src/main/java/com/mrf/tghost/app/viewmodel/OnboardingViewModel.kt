package com.mrf.tghost.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrf.tghost.app.ui.composables.ViewState
import com.mrf.tghost.domain.model.Result
import com.mrf.tghost.domain.usecase.preferences.GetOnboardingStatusUseCase
import com.mrf.tghost.domain.usecase.preferences.SaveOnboardingCompletedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val getOnboardingStatusUseCase: GetOnboardingStatusUseCase,
    private val saveOnboardingCompletedUseCase: SaveOnboardingCompletedUseCase
): ViewModel() {

    private val _onboardingCompletedFlag = MutableStateFlow<Result<Boolean>>(Result.Loading)
    val onboardingCompletedFlag: StateFlow<Result<Boolean>> = _onboardingCompletedFlag

    private val _viewState = MutableStateFlow<ViewState>(ViewState.None)
    val viewState: StateFlow<ViewState> = _viewState

    fun getOnboardingStatus() {
        viewModelScope.launch {
            getOnboardingStatusUseCase()
                .catch { e ->
                    _onboardingCompletedFlag.value = Result.Failure(e.message ?: "Unknown error")
                }
                .collect { status ->
                    _onboardingCompletedFlag.value = if (status) Result.Success(true) else Result.Failure("Incomplete")
                }
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            saveOnboardingCompletedUseCase(true)
            _viewState.value = ViewState.Success
        }
    }
}