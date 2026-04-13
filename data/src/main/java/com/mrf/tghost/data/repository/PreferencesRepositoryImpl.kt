package com.mrf.tghost.data.repository

import com.mrf.tghost.data.datastore.DataStore
import com.mrf.tghost.domain.model.NetworkType
import com.mrf.tghost.domain.model.RpcPreference
import com.mrf.tghost.domain.model.RpcProviderId
import com.mrf.tghost.domain.model.SupportedChainId
import com.mrf.tghost.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class PreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore
) : PreferencesRepository {

    override fun getLiveUpdateStatus(): Flow<Boolean> {
        return dataStore.getLiveUpdateStatus
    }

    override suspend fun setLiveUpdateStatus(enabled: Boolean) {
        dataStore.saveLiveUpdateStatus(enabled)
    }

    override fun getOnboardingCompleted(): Flow<Boolean> {
        return dataStore.getOnboardingStatus
    }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.saveOnboardingCompleted(completed)
    }

    override fun getRpcPreference(chainId: SupportedChainId): Flow<RpcPreference> {
        val providerFlow: Flow<String>
        val networkFlow: Flow<String>
        when (chainId) {
            SupportedChainId.SOL -> {
                providerFlow = dataStore.getSolanaRpcProviderId
                networkFlow = dataStore.getSolanaRpcNetworkType
            }
            SupportedChainId.EVM -> {
                providerFlow = dataStore.getEvmRpcProviderId
                networkFlow = dataStore.getEvmRpcNetworkType
            }
            SupportedChainId.SUI -> {
                providerFlow = dataStore.getSuiRpcProviderId
                networkFlow = dataStore.getSuiRpcNetworkType
            }
            SupportedChainId.TEZ -> {
                providerFlow = dataStore.getTezosRpcProviderId
                networkFlow = dataStore.getTezosRpcNetworkType
            }
        }
        return combine(providerFlow, networkFlow) { providerIdStr, networkTypeStr ->
            RpcPreference(
                providerId = parseRpcProviderId(providerIdStr),
                networkType = parseNetworkType(networkTypeStr),
            )
        }
    }

    override suspend fun setRpcPreference(chainId: SupportedChainId, preference: RpcPreference) {
        val providerIdStr = preference.providerId.name
        val networkTypeStr = preference.networkType.name
        when (chainId) {
            SupportedChainId.SOL -> dataStore.saveSolanaRpcPreference(providerIdStr, networkTypeStr)
            SupportedChainId.EVM -> dataStore.saveEvmRpcPreference(providerIdStr, networkTypeStr)
            SupportedChainId.SUI -> dataStore.saveSuiRpcPreference(providerIdStr, networkTypeStr)
            SupportedChainId.TEZ -> dataStore.saveTezosRpcPreference(providerIdStr, networkTypeStr)
        }
    }

    override fun getRpcProviderApiKey(providerId: RpcProviderId): Flow<String?> =
        dataStore.getRpcProviderApiKey(providerId.name)

    override suspend fun setRpcProviderApiKey(providerId: RpcProviderId, value: String) {
        dataStore.setRpcProviderApiKey(providerId.name, value)
    }

    private fun parseRpcProviderId(value: String): RpcProviderId =
        try {
            RpcProviderId.valueOf(value)
        } catch (_: IllegalArgumentException) {
            RpcProviderId.OFFICIAL
        }

    private fun parseNetworkType(value: String): NetworkType =
        try {
            NetworkType.valueOf(value)
        } catch (_: IllegalArgumentException) {
            NetworkType.MAINNET
        }
}
