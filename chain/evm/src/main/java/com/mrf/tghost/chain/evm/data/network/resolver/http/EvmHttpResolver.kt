package com.mrf.tghost.chain.evm.data.network.resolver.http

import com.mrf.tghost.domain.model.EvmChain
import com.mrf.tghost.domain.model.NetworkType
import com.mrf.tghost.domain.model.RpcPreference
import com.mrf.tghost.domain.model.RpcProviderId
import com.mrf.tghost.domain.model.SupportedChain
import com.mrf.tghost.domain.repository.PreferencesRepository
import com.mrf.tghost.chain.evm.utils.BASE_MAINNET_RPC_URL_ALCHEMY
import com.mrf.tghost.chain.evm.utils.BASE_MAINNET_RPC_URL_ANKR
import com.mrf.tghost.chain.evm.utils.BASE_MAINNET_RPC_URL_DRPC
import com.mrf.tghost.chain.evm.utils.BASE_MAINNET_RPC_URL_PUBLIC_NODE
import com.mrf.tghost.chain.evm.utils.ETHEREUM_MAINNET_RPC_URL_ALCHEMY
import com.mrf.tghost.chain.evm.utils.ETHEREUM_MAINNET_RPC_URL_ANKR
import com.mrf.tghost.chain.evm.utils.ETHEREUM_MAINNET_RPC_URL_DRPC
import com.mrf.tghost.chain.evm.utils.ETHEREUM_MAINNET_RPC_URL_PUBLIC_NODE
import com.mrf.tghost.domain.model.SupportedChainId
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class EvmHttpResolver @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
) {

    suspend fun resolveEvmUrl(evmChainId: EvmChain): String {
        val preference: RpcPreference =
            preferencesRepository.getRpcPreference(SupportedChainId.EVM).first()
        val apiKey: String? =
            preferencesRepository.getRpcProviderApiKey(preference.providerId).first()

        return when (preference.providerId) {
            RpcProviderId.ALCHEMY -> evmAlchemyUrl(preference.networkType, apiKey, evmChainId)
            RpcProviderId.ANKR -> evmAnkrUrl(preference.networkType, evmChainId)
            RpcProviderId.DRPC -> evmDrpcUrl(preference.networkType, evmChainId)
            RpcProviderId.PUBLIC_NODE -> evmPublicNodeUrl(preference.networkType, evmChainId)
            // Unsupported for EVM – fall back to Ethereum mainnet PublicNode.
            RpcProviderId.OFFICIAL,
            RpcProviderId.HELIUS,
            RpcProviderId.ECAD_INFRA,
                -> ETHEREUM_MAINNET_RPC_URL_PUBLIC_NODE
        }
    }

    private fun evmAlchemyUrl(networkType: NetworkType, apiKey: String?, evmChainId: EvmChain): String {
        require(!apiKey.isNullOrBlank()) {
            "Missing API key for Alchemy EVM RPC provider"
        }
        if (networkType != NetworkType.MAINNET) {
            throw IllegalArgumentException("Alchemy EVM resolver supports MAINNET only")
        }
        val baseUrl = when (evmChainId) {
            EvmChain.ETHEREUM -> ETHEREUM_MAINNET_RPC_URL_ALCHEMY
            EvmChain.BASE -> BASE_MAINNET_RPC_URL_ALCHEMY
        }
        return baseUrl + apiKey
    }

    private fun evmAnkrUrl(networkType: NetworkType, evmChainId: EvmChain): String {
        if (networkType != NetworkType.MAINNET) {
            throw IllegalArgumentException("Ankr EVM resolver supports MAINNET only")
        }
        return when (evmChainId) {
            EvmChain.ETHEREUM -> ETHEREUM_MAINNET_RPC_URL_ANKR
            EvmChain.BASE -> BASE_MAINNET_RPC_URL_ANKR
        }
    }

    private fun evmDrpcUrl(networkType: NetworkType, evmChainId: EvmChain): String {
        if (networkType != NetworkType.MAINNET) {
            throw IllegalArgumentException("dRPC EVM resolver supports MAINNET only")
        }
        return when (evmChainId) {
            EvmChain.ETHEREUM -> ETHEREUM_MAINNET_RPC_URL_DRPC
            EvmChain.BASE -> BASE_MAINNET_RPC_URL_DRPC
        }
    }

    private fun evmPublicNodeUrl(networkType: NetworkType, evmChainId: EvmChain): String {
        if (networkType != NetworkType.MAINNET) {
            throw IllegalArgumentException("PublicNode EVM resolver supports MAINNET only")
        }
        return when (evmChainId) {
            EvmChain.ETHEREUM -> ETHEREUM_MAINNET_RPC_URL_PUBLIC_NODE
            EvmChain.BASE -> BASE_MAINNET_RPC_URL_PUBLIC_NODE
        }
    }
}