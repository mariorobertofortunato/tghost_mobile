package com.mrf.tghost.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrf.tghost.app.contracts.PreferencesEvent
import com.mrf.tghost.app.contracts.PreferencesState
import com.mrf.tghost.domain.model.RpcPreference
import com.mrf.tghost.domain.model.RpcProviderId
import com.mrf.tghost.domain.model.SupportedChainId
import com.mrf.tghost.domain.usecase.preferences.GetLiveUpdateStatusUseCase
import com.mrf.tghost.domain.usecase.preferences.GetRpcPreferenceUseCase
import com.mrf.tghost.domain.usecase.preferences.GetRpcProviderApiKeyUseCase
import com.mrf.tghost.domain.usecase.preferences.SaveLiveUpdateStatus
import com.mrf.tghost.domain.usecase.preferences.SaveRpcPreferenceUseCase
import com.mrf.tghost.domain.usecase.preferences.SaveRpcProviderApiKeyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreferencesViewModel @Inject constructor(
    private val getRpcPreferenceUseCase: GetRpcPreferenceUseCase,
    private val saveRpcPreferenceUseCase: SaveRpcPreferenceUseCase,
    private val getRpcProviderApiKeyUseCase: GetRpcProviderApiKeyUseCase,
    private val saveRpcProviderApiKeyUseCase: SaveRpcProviderApiKeyUseCase,
    private val getLiveUpdateStatusUseCase: GetLiveUpdateStatusUseCase,
    private val saveLiveUpdateStatus: SaveLiveUpdateStatus
) : ViewModel() {

    private val _state = MutableStateFlow(
        PreferencesState(
            eventTunnel = { event -> processEvent(event) }
        )
    )
    val state: StateFlow<PreferencesState> = _state.asStateFlow()

    init {
        fetchRpcPreferences()
        fetchLiveUpdateStatus()
    }

    private fun processEvent(event: PreferencesEvent) {
        when (event) {
            is PreferencesEvent.FetchRpcPreferences -> fetchRpcPreferences()
            is PreferencesEvent.SaveRpcPreference -> saveRpcPreference(event.chainId, event.preference)
            is PreferencesEvent.SaveRpcProviderApiKey -> saveRpcProviderApiKey(event.providerId, event.value)
            is PreferencesEvent.FetchLiveUpdateStatus -> fetchLiveUpdateStatus()
            is PreferencesEvent.ToggleLiveUpdate -> toggleLiveUpdate()
        }
    }

    private fun fetchLiveUpdateStatus() {
        viewModelScope.launch(Dispatchers.IO) {
            getLiveUpdateStatusUseCase().collect { enabled ->
                _state.update {
                    it.copy(liveUpdateEnabled = enabled)
                }
            }
        }
    }

    private fun toggleLiveUpdate() {
        viewModelScope.launch(Dispatchers.IO) {
            saveLiveUpdateStatus(!_state.value.liveUpdateEnabled)
        }
    }

    private fun fetchRpcPreferences() {
        viewModelScope.launch(Dispatchers.IO) {
            val prefsFlow = combine(
                getRpcPreferenceUseCase(SupportedChainId.SOL),
                getRpcPreferenceUseCase(SupportedChainId.EVM),
                getRpcPreferenceUseCase(SupportedChainId.SUI),
                getRpcPreferenceUseCase(SupportedChainId.TEZ),
            ) { solana, evm, sui, tez ->
                mapOf(
                    SupportedChainId.SOL to solana,
                    SupportedChainId.EVM to evm,
                    SupportedChainId.SUI to sui,
                    SupportedChainId.TEZ to tez,
                )
            }
            val apiKeysFlow = combine(
                getRpcProviderApiKeyUseCase(RpcProviderId.HELIUS),
                getRpcProviderApiKeyUseCase(RpcProviderId.ALCHEMY),
                getRpcProviderApiKeyUseCase(RpcProviderId.ANKR),
                getRpcProviderApiKeyUseCase(RpcProviderId.DRPC),
            ) { keyHelius, keyAlchemy, keyAnkr, keyDrpc ->
                mapOf(
                    RpcProviderId.HELIUS.name to (keyHelius ?: ""),
                    RpcProviderId.ALCHEMY.name to (keyAlchemy ?: ""),
                    RpcProviderId.ANKR.name to (keyAnkr ?: ""),
                    RpcProviderId.DRPC.name to (keyDrpc ?: ""),
                )
            }
            combine(prefsFlow, apiKeysFlow) { prefs, apiKeys ->
                _state.update { it.copy(rpcPreferences = prefs, rpcProviderApiKeys = apiKeys) }
            }.collect {}
        }
    }

    private fun saveRpcPreference(chainId: SupportedChainId, preference: RpcPreference) {
        viewModelScope.launch(Dispatchers.IO) {
            saveRpcPreferenceUseCase(chainId, preference)
        }
    }

    private fun saveRpcProviderApiKey(providerId: RpcProviderId, value: String) {
        viewModelScope.launch(Dispatchers.IO) {
            saveRpcProviderApiKeyUseCase(providerId, value)
        }
    }
}
