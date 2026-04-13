package com.mrf.tghost.chain.sui.data.network.resolver.http

import com.mrf.tghost.chain.sui.utils.SUI_MAINNET_RPC_URL
import com.mrf.tghost.chain.sui.utils.SUI_MAINNET_GRAPHQL_URL
import com.mrf.tghost.domain.model.NetworkType
import com.mrf.tghost.domain.model.RpcPreference
import com.mrf.tghost.domain.model.RpcProviderId
import com.mrf.tghost.domain.model.SupportedChain
import com.mrf.tghost.domain.model.SupportedChainId
import com.mrf.tghost.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SuiHttpResolver @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
) {

    // TODO this doesn't make much sense if Sui only has one rpc provider
    suspend fun resolveSuiUrl(): String {
        val preference: RpcPreference =
            preferencesRepository.getRpcPreference(SupportedChainId.SUI).first()
        val apiKey: String? =
            preferencesRepository.getRpcProviderApiKey(preference.providerId).first()

        return when (preference.providerId) {
            RpcProviderId.OFFICIAL -> suiOfficialUrl(preference.networkType)
            else -> suiOfficialUrl(preference.networkType)
        }
    }

    // TODO confirm there's no devnet or testnet?
    private fun suiOfficialUrl(networkType: NetworkType): String =
        when (networkType) {
            NetworkType.MAINNET, NetworkType.TESTNET, NetworkType.DEVNET -> SUI_MAINNET_RPC_URL
        }

    suspend fun resolveSuiGraphQlUrl(): String {
        val preference: RpcPreference =
            preferencesRepository.getRpcPreference(SupportedChainId.SUI).first()
        return when (preference.providerId) {
            RpcProviderId.OFFICIAL -> suiOfficialGraphQlUrl(preference.networkType)
            else -> suiOfficialGraphQlUrl(preference.networkType)
        }
    }

    private fun suiOfficialGraphQlUrl(networkType: NetworkType): String =
        when (networkType) {
            NetworkType.MAINNET, NetworkType.TESTNET, NetworkType.DEVNET -> SUI_MAINNET_GRAPHQL_URL
        }

}