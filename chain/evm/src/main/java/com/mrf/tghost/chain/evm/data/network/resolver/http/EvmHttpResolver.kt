package com.mrf.tghost.chain.evm.data.network.resolver.http

import com.mrf.tghost.chain.evm.utils.ALCHEMY_API_URL
import com.mrf.tghost.chain.evm.utils.ALCHEMY_RPC_BASE_URL
import com.mrf.tghost.chain.evm.utils.ALCHEMY_RPC_ETHEREUM_URL
import com.mrf.tghost.chain.evm.utils.BASE_MAINNET_RPC_URL_PUBLIC_NODE
import com.mrf.tghost.chain.evm.utils.ETHEREUM_MAINNET_RPC_URL_PUBLIC_NODE
import com.mrf.tghost.chain.evm.utils.MORALIS_API_URL
import com.mrf.tghost.domain.model.EvmChain
import com.mrf.tghost.domain.model.NetworkType
import com.mrf.tghost.domain.model.RpcPreference
import com.mrf.tghost.domain.model.RpcProviderId
import com.mrf.tghost.domain.model.SupportedChainId
import com.mrf.tghost.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class EvmHttpResolver @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
) {

    suspend fun resolveAlchemyRpcUrl(evmChainId: EvmChain?): String {
        val preference: RpcPreference =
            preferencesRepository.getRpcPreference(SupportedChainId.EVM).first()
        val apiKey: String? =
            preferencesRepository.getRpcProviderApiKey(preference.providerId).first()

        require(!apiKey.isNullOrBlank()) {
            "Missing API key for Alchemy EVM RPC provider"
        }
        if (preference.networkType != NetworkType.MAINNET) {
            throw IllegalArgumentException("Alchemy EVM resolver supports MAINNET only")
        }
        val baseRpcUrl = when (evmChainId) {
            EvmChain.ETHEREUM -> ALCHEMY_RPC_ETHEREUM_URL
            EvmChain.BASE -> ALCHEMY_RPC_BASE_URL
            else -> ALCHEMY_RPC_ETHEREUM_URL
        }

        return "$baseRpcUrl$apiKey"

    }

    suspend fun resolveEvmUrl(evmChainId: EvmChain?): Pair<String, String?> {
        val preference: RpcPreference =
            preferencesRepository.getRpcPreference(SupportedChainId.EVM).first()
        val apiKey: String? =
            preferencesRepository.getRpcProviderApiKey(preference.providerId).first()

        return when (preference.providerId) {
            RpcProviderId.ALCHEMY -> evmAlchemyUrl(preference.networkType, apiKey?.trim())
            RpcProviderId.PUBLIC_NODE -> evmPublicNodeUrl(preference.networkType, evmChainId)
            RpcProviderId.MORALIS -> evmMoralisUrl(preference.networkType, apiKey?.trim())
            else -> Pair(ETHEREUM_MAINNET_RPC_URL_PUBLIC_NODE, null)
        }
    }

    private fun evmAlchemyUrl(
        networkType: NetworkType,
        apiKey: String?
    ): Pair<String, String> {
        require(!apiKey.isNullOrBlank()) {
            "Missing API key for Alchemy provider"
        }
        if (networkType != NetworkType.MAINNET) {
            throw IllegalArgumentException("Alchemy EVM resolver supports MAINNET only")
        }
        return Pair(ALCHEMY_API_URL, apiKey)
    }

    private fun evmPublicNodeUrl(
        networkType: NetworkType,
        evmChainId: EvmChain?
    ): Pair<String, String?> {
        if (networkType != NetworkType.MAINNET) {
            throw IllegalArgumentException("PublicNode EVM resolver supports MAINNET only")
        }
        return when (evmChainId) {
            EvmChain.ETHEREUM -> Pair(ETHEREUM_MAINNET_RPC_URL_PUBLIC_NODE, null)
            EvmChain.BASE -> Pair(BASE_MAINNET_RPC_URL_PUBLIC_NODE, null)
            else -> Pair(ETHEREUM_MAINNET_RPC_URL_PUBLIC_NODE, null)
        }
    }

    private fun evmMoralisUrl(
        networkType: NetworkType,
        apiKey: String?,
    ): Pair<String, String> {
        require(!apiKey.isNullOrBlank()) {
            "Missing API key for Moralis provider"
        }
        if (networkType != NetworkType.MAINNET) {
            throw IllegalArgumentException("PublicNode EVM resolver supports MAINNET only")
        }
        return Pair(MORALIS_API_URL, apiKey)
    }
}