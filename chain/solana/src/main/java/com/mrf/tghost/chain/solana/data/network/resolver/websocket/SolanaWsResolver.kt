package com.mrf.tghost.chain.solana.data.network.resolver.websocket

import com.mrf.tghost.chain.solana.utils.SOLANA_DEVNET_WS_URL
import com.mrf.tghost.chain.solana.utils.SOLANA_DEVNET_WS_URL_HELIUS
import com.mrf.tghost.chain.solana.utils.SOLANA_MAINNET_WS_URL
import com.mrf.tghost.chain.solana.utils.SOLANA_MAINNET_WS_URL_DRPC_BASE
import com.mrf.tghost.chain.solana.utils.SOLANA_MAINNET_WS_URL_HELIUS
import com.mrf.tghost.chain.solana.utils.SOLANA_MAINNET_WS_URL_PUBLIC_NODE
import com.mrf.tghost.chain.solana.utils.SOLANA_TESTNET_WS_URL
import com.mrf.tghost.chain.solana.utils.SOLANA_TESTNET_WS_URL_HELIUS
import com.mrf.tghost.domain.model.NetworkType
import com.mrf.tghost.domain.model.RpcPreference
import com.mrf.tghost.domain.model.RpcProviderId
import com.mrf.tghost.domain.model.SupportedChainId
import com.mrf.tghost.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SolanaWsResolver @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
) {

    suspend fun resolveSolanaWsUrl(): String? {
        val preference: RpcPreference =
            preferencesRepository.getRpcPreference(SupportedChainId.SOL).first()
        val apiKey: String? =
            preferencesRepository.getRpcProviderApiKey(preference.providerId).first()

        return when (preference.providerId) {
            RpcProviderId.OFFICIAL -> solanaOfficialWs(preference.networkType)
            RpcProviderId.HELIUS -> solanaHeliusWs(preference.networkType, apiKey)
            RpcProviderId.PUBLIC_NODE -> SOLANA_MAINNET_WS_URL_PUBLIC_NODE
            else -> solanaOfficialWs(preference.networkType)
        }
    }

    private fun solanaOfficialWs(networkType: NetworkType): String =
        when (networkType) {
            NetworkType.MAINNET -> SOLANA_MAINNET_WS_URL
            NetworkType.TESTNET -> SOLANA_TESTNET_WS_URL
            NetworkType.DEVNET -> SOLANA_DEVNET_WS_URL
        }

    private fun solanaHeliusWs(networkType: NetworkType, apiKey: String?): String {
        require(!apiKey.isNullOrBlank()) {
            "Missing API key for Helius Solana WebSocket"
        }
        val base = when (networkType) {
            NetworkType.MAINNET -> SOLANA_MAINNET_WS_URL_HELIUS
            NetworkType.TESTNET -> SOLANA_TESTNET_WS_URL_HELIUS
            NetworkType.DEVNET -> SOLANA_DEVNET_WS_URL_HELIUS
        }
        return "$base?api-key=$apiKey"
    }

}