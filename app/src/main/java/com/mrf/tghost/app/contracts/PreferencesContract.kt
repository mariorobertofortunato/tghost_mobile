package com.mrf.tghost.app.contracts

import androidx.compose.runtime.Immutable
import com.mrf.tghost.domain.model.RpcPreference
import com.mrf.tghost.domain.model.RpcProviderId
import com.mrf.tghost.domain.model.SupportedChainId

@Immutable
sealed class PreferencesEvent {
    object FetchRpcPreferences : PreferencesEvent()
    data class SaveRpcPreference(val chainId: SupportedChainId, val preference: RpcPreference) : PreferencesEvent()
    data class SaveRpcProviderApiKey(val providerId: RpcProviderId, val value: String) : PreferencesEvent()
    object FetchLiveUpdateStatus : PreferencesEvent()
    object ToggleLiveUpdate : PreferencesEvent()
}

data class PreferencesState(
    val rpcPreferences: Map<SupportedChainId, RpcPreference> = emptyMap(),
    val rpcProviderApiKeys: Map<String, String> = emptyMap(),
    val liveUpdateEnabled: Boolean = false,
    val eventTunnel: (PreferencesEvent) -> Unit = {},
)
