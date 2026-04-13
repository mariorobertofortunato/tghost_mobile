package com.mrf.tghost.domain.repository

import com.mrf.tghost.domain.model.RpcPreference
import com.mrf.tghost.domain.model.RpcProviderId
import com.mrf.tghost.domain.model.SupportedChainId
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    fun getOnboardingCompleted(): Flow<Boolean>
    suspend fun setOnboardingCompleted(completed: Boolean)
    fun getLiveUpdateStatus(): Flow<Boolean>
    suspend fun setLiveUpdateStatus(enabled: Boolean)
    fun getRpcPreference(chainId: SupportedChainId): Flow<RpcPreference>
    suspend fun setRpcPreference(chainId: SupportedChainId, preference: RpcPreference)
    fun getRpcProviderApiKey(providerId: RpcProviderId): Flow<String?>
    suspend fun setRpcProviderApiKey(providerId: RpcProviderId, value: String)
}
