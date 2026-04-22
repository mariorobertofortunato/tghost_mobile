package com.mrf.tghost.chain.solana.data.network.resolver.http

import com.mrf.tghost.chain.solana.utils.SOLANA_DEVNET_RPC_URL
import com.mrf.tghost.chain.solana.utils.SOLANA_DEVNET_RPC_URL_HELIUS
import com.mrf.tghost.chain.solana.utils.SOLANA_MAINNET_RPC_URL
import com.mrf.tghost.chain.solana.utils.SOLANA_MAINNET_RPC_URL_HELIUS
import com.mrf.tghost.chain.solana.utils.SOLANA_MAINNET_RPC_URL_PUBLIC_NODE
import com.mrf.tghost.chain.solana.utils.SOLANA_TESTNET_RPC_URL
import com.mrf.tghost.chain.solana.utils.SOLANA_TESTNET_RPC_URL_HELIUS
import com.mrf.tghost.domain.model.NetworkType
import com.mrf.tghost.domain.model.RpcPreference
import com.mrf.tghost.domain.model.RpcProviderId
import com.mrf.tghost.domain.model.SupportedChainId
import com.mrf.tghost.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SolanaHttpResolver @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
) {

    suspend fun resolveSolanaUrl(): String {
        val preference: RpcPreference =
            preferencesRepository.getRpcPreference(SupportedChainId.SOL).first()
        val apiKey: String? =
            preferencesRepository.getRpcProviderApiKey(preference.providerId).first()

        return when (preference.providerId) {
            RpcProviderId.OFFICIAL -> solanaOfficialUrl(preference.networkType)
            RpcProviderId.HELIUS -> solanaHeliusUrl(preference.networkType, apiKey)
            RpcProviderId.PUBLIC_NODE -> solanaPublicNodeUrl(preference.networkType)
            // Currently unsupported for Solana – fall back to official mainnet.
            else -> SOLANA_MAINNET_RPC_URL
        }
    }

    private fun solanaOfficialUrl(networkType: NetworkType): String =
        when (networkType) {
            NetworkType.MAINNET -> SOLANA_MAINNET_RPC_URL
            NetworkType.TESTNET -> SOLANA_TESTNET_RPC_URL
            NetworkType.DEVNET -> SOLANA_DEVNET_RPC_URL
        }

    private fun solanaHeliusUrl(networkType: NetworkType, apiKey: String?): String {
        require(!apiKey.isNullOrBlank()) {
            "Missing API key for Helius Solana RPC provider"
        }
        val baseUrl = when (networkType) {
            NetworkType.MAINNET -> SOLANA_MAINNET_RPC_URL_HELIUS
            NetworkType.TESTNET -> SOLANA_TESTNET_RPC_URL_HELIUS
            NetworkType.DEVNET -> SOLANA_DEVNET_RPC_URL_HELIUS
        }
        // Helius expects the API key as a query parameter (?api-key=KEY).
        return "${baseUrl}?api-key=$apiKey"
    }

    private fun solanaPublicNodeUrl(networkType: NetworkType): String {
        // PublicNode currently exposes only mainnet for Solana.
        return when (networkType) {
            NetworkType.MAINNET -> SOLANA_MAINNET_RPC_URL_PUBLIC_NODE
            NetworkType.TESTNET,
            NetworkType.DEVNET,
                -> SOLANA_MAINNET_RPC_URL_PUBLIC_NODE
        }
    }

}