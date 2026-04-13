package com.mrf.tghost.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.mrf.tghost.data.utils.EVM_RPC_NETWORK_TYPE_KEY
import com.mrf.tghost.data.utils.EVM_RPC_PROVIDER_ID_KEY
import com.mrf.tghost.data.utils.LIVE_UPDATE_STATUS_KEY
import com.mrf.tghost.data.utils.ONBOARDING_STATUS_KEY
import com.mrf.tghost.data.utils.RPC_API_KEY_PREFIX
import com.mrf.tghost.data.utils.SOLANA_RPC_NETWORK_TYPE_KEY
import com.mrf.tghost.data.utils.SOLANA_RPC_PROVIDER_ID_KEY
import com.mrf.tghost.data.utils.SUI_RPC_NETWORK_TYPE_KEY
import com.mrf.tghost.data.utils.SUI_RPC_PROVIDER_ID_KEY
import com.mrf.tghost.data.utils.TEZOS_RPC_NETWORK_TYPE_KEY
import com.mrf.tghost.data.utils.TEZOS_RPC_PROVIDER_ID_KEY
import com.mrf.tghost.data.utils.USER_PREFERENCES_DATASTORE_KEY
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class DataStore(private val context: Context) {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
            USER_PREFERENCES_DATASTORE_KEY
        )
        private val ONBOARDING_STATUS = booleanPreferencesKey(ONBOARDING_STATUS_KEY)
        private val LIVE_UPDATE_ENABLED = booleanPreferencesKey(LIVE_UPDATE_STATUS_KEY)

        // RPC preferences: provider id + network type
        private val SOLANA_RPC_PROVIDER_ID = stringPreferencesKey(SOLANA_RPC_PROVIDER_ID_KEY)
        private val SOLANA_RPC_NETWORK_TYPE = stringPreferencesKey(SOLANA_RPC_NETWORK_TYPE_KEY)
        private val EVM_RPC_PROVIDER_ID = stringPreferencesKey(EVM_RPC_PROVIDER_ID_KEY)
        private val EVM_RPC_NETWORK_TYPE = stringPreferencesKey(EVM_RPC_NETWORK_TYPE_KEY)
        private val SUI_RPC_PROVIDER_ID = stringPreferencesKey(SUI_RPC_PROVIDER_ID_KEY)
        private val SUI_RPC_NETWORK_TYPE = stringPreferencesKey(SUI_RPC_NETWORK_TYPE_KEY)
        private val TEZOS_RPC_PROVIDER_ID = stringPreferencesKey(TEZOS_RPC_PROVIDER_ID_KEY)
        private val TEZOS_RPC_NETWORK_TYPE = stringPreferencesKey(TEZOS_RPC_NETWORK_TYPE_KEY)

        private const val DEFAULT_PROVIDER_ID = "OFFICIAL"
        private const val DEFAULT_NETWORK_TYPE = "MAINNET"
    }

    val getLiveUpdateStatus: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[LIVE_UPDATE_ENABLED] ?: false
    }.distinctUntilChanged()

    suspend fun saveLiveUpdateStatus(status: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[LIVE_UPDATE_ENABLED] = status
        }
    }

    val getOnboardingStatus: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[ONBOARDING_STATUS] ?: false
    }

    suspend fun saveOnboardingCompleted(status: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ONBOARDING_STATUS] = status
        }
    }

    // --- RPC preferences (provider + network)

    val getSolanaRpcProviderId: Flow<String> = context.dataStore.data.map {
        it[SOLANA_RPC_PROVIDER_ID] ?: DEFAULT_PROVIDER_ID
    }
    val getSolanaRpcNetworkType: Flow<String> = context.dataStore.data.map {
        it[SOLANA_RPC_NETWORK_TYPE] ?: DEFAULT_NETWORK_TYPE
    }
    suspend fun saveSolanaRpcPreference(providerId: String, networkType: String) {
        context.dataStore.edit {
            it[SOLANA_RPC_PROVIDER_ID] = providerId
            it[SOLANA_RPC_NETWORK_TYPE] = networkType
        }
    }

    val getEvmRpcProviderId: Flow<String> = context.dataStore.data.map {
        it[EVM_RPC_PROVIDER_ID] ?: "PUBLIC_NODE"
    }
    val getEvmRpcNetworkType: Flow<String> = context.dataStore.data.map {
        it[EVM_RPC_NETWORK_TYPE] ?: DEFAULT_NETWORK_TYPE
    }
    suspend fun saveEvmRpcPreference(providerId: String, networkType: String) {
        context.dataStore.edit {
            it[EVM_RPC_PROVIDER_ID] = providerId
            it[EVM_RPC_NETWORK_TYPE] = networkType
        }
    }

    val getSuiRpcProviderId: Flow<String> = context.dataStore.data.map {
        it[SUI_RPC_PROVIDER_ID] ?: DEFAULT_PROVIDER_ID
    }
    val getSuiRpcNetworkType: Flow<String> = context.dataStore.data.map {
        it[SUI_RPC_NETWORK_TYPE] ?: DEFAULT_NETWORK_TYPE
    }
    suspend fun saveSuiRpcPreference(providerId: String, networkType: String) {
        context.dataStore.edit {
            it[SUI_RPC_PROVIDER_ID] = providerId
            it[SUI_RPC_NETWORK_TYPE] = networkType
        }
    }

    val getTezosRpcProviderId: Flow<String> = context.dataStore.data.map {
        it[TEZOS_RPC_PROVIDER_ID] ?: DEFAULT_PROVIDER_ID
    }
    val getTezosRpcNetworkType: Flow<String> = context.dataStore.data.map {
        it[TEZOS_RPC_NETWORK_TYPE] ?: DEFAULT_NETWORK_TYPE
    }
    suspend fun saveTezosRpcPreference(providerId: String, networkType: String) {
        context.dataStore.edit {
            it[TEZOS_RPC_PROVIDER_ID] = providerId
            it[TEZOS_RPC_NETWORK_TYPE] = networkType
        }
    }

    // --- RPC provider API key: one preference key per provider

    fun getRpcProviderApiKey(providerIdKey: String): Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[stringPreferencesKey(RPC_API_KEY_PREFIX + providerIdKey.lowercase())]?.takeIf { it.isNotBlank() }
    }

    suspend fun setRpcProviderApiKey(providerIdKey: String, value: String) {
        context.dataStore.edit { prefs ->
            val key = stringPreferencesKey(RPC_API_KEY_PREFIX + providerIdKey.lowercase())
            if (value.isBlank()) prefs.remove(key) else prefs[key] = value
        }
    }
}