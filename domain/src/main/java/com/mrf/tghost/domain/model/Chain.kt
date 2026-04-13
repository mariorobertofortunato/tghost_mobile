package com.mrf.tghost.domain.model

import kotlinx.serialization.Serializable

enum class NetworkType {
    MAINNET, DEVNET, TESTNET
}

/**
 * Identifies an RPC provider. Used by app/data layer to resolve actual RPC URLs
 * (e.g. from Constants or user API key).
 */
@Serializable
enum class RpcProviderId {
    OFFICIAL,
    HELIUS,
    ALCHEMY,
    ANKR,
    DRPC,
    ECAD_INFRA,
    PUBLIC_NODE,
}

/**
 * Describes an RPC provider option for a chain: display name, whether it requires
 * an API key, and which network types it supports. Actual URL resolution is done
 * in app/data layer (e.g. a resolver that uses URL constants and optional API key).
 */

data class RpcProviderOption(
    val id: RpcProviderId,
    val displayName: String,
    val requiresApiKey: Boolean,
    val supportedNetworks: List<NetworkType> = listOf(NetworkType.MAINNET),
)


data class Chain(
    val id: SupportedChainId,
    val name: String = "",
    val symbol: String = "",
    val rpcProviders: List<RpcProviderOption> = emptyList(),
)

@Serializable
enum class SupportedChainId {
    SOL, EVM, SUI, TEZ
}

enum class SupportedChain(val chain: Chain) {
    SOLANA(
        chain = Chain(
            id = SupportedChainId.SOL,
            name = "Solana",
            symbol = "SOL",
            rpcProviders = listOf(
                RpcProviderOption(
                    id = RpcProviderId.OFFICIAL,
                    displayName = "Solana (official)",
                    requiresApiKey = false,
                    supportedNetworks = listOf(NetworkType.MAINNET, NetworkType.TESTNET, NetworkType.DEVNET),
                ),
                RpcProviderOption(
                    id = RpcProviderId.HELIUS,
                    displayName = "Helius",
                    requiresApiKey = true,
                    supportedNetworks = listOf(NetworkType.MAINNET, NetworkType.TESTNET, NetworkType.DEVNET),
                ),
/*                RpcProviderOption(
                    id = RpcProviderId.ALCHEMY,
                    displayName = "Alchemy",
                    requiresApiKey = true,
                    supportedNetworks = listOf(NetworkType.MAINNET, NetworkType.DEVNET),
                ),*/
                RpcProviderOption(
                    id = RpcProviderId.DRPC,
                    displayName = "dRPC",
                    requiresApiKey = false,
                    supportedNetworks = listOf(NetworkType.MAINNET),
                ),
                RpcProviderOption(
                    id = RpcProviderId.PUBLIC_NODE,
                    displayName = "PublicNode",
                    requiresApiKey = false,
                    supportedNetworks = listOf(NetworkType.MAINNET),
                ),
            ),
        )
    ),
    EVM(
        chain = Chain(
            id = SupportedChainId.EVM,
            name = "Evm",
            symbol = "ETH",
            rpcProviders = listOf(
                RpcProviderOption(
                    id = RpcProviderId.ALCHEMY,
                    displayName = "Alchemy",
                    requiresApiKey = true,
                    supportedNetworks = listOf(NetworkType.MAINNET),
                ),
                RpcProviderOption(
                    id = RpcProviderId.ANKR,
                    displayName = "Ankr",
                    requiresApiKey = true,
                    supportedNetworks = listOf(NetworkType.MAINNET),
                ),
                RpcProviderOption(
                    id = RpcProviderId.DRPC,
                    displayName = "dRPC",
                    requiresApiKey = true,
                    supportedNetworks = listOf(NetworkType.MAINNET),
                ),
                RpcProviderOption(
                    id = RpcProviderId.PUBLIC_NODE,
                    displayName = "PublicNode",
                    requiresApiKey = false,
                    supportedNetworks = listOf(NetworkType.MAINNET),
                ),
            ),
        )
    ),
    SUI(
        chain = Chain(
            id = SupportedChainId.SUI,
            name = "Sui",
            symbol = "SUI",
            rpcProviders = listOf(
                RpcProviderOption(
                    id = RpcProviderId.OFFICIAL,
                    displayName = "Sui (official)",
                    requiresApiKey = false,
                    supportedNetworks = listOf(NetworkType.MAINNET),
                )
            ),
        )
    ),
    TEZ(
        chain = Chain(
            id = SupportedChainId.TEZ,
            name = "Tezos",
            symbol = "TEZ",
            rpcProviders = listOf(
                RpcProviderOption(
                    id = RpcProviderId.OFFICIAL,
                    displayName = "TzKT (official)",
                    requiresApiKey = false,
                    supportedNetworks = listOf(NetworkType.MAINNET),
                ),
                RpcProviderOption(
                    id = RpcProviderId.ECAD_INFRA,
                    displayName = "ECAD Infra",
                    requiresApiKey = false,
                    supportedNetworks = listOf(NetworkType.MAINNET, NetworkType.TESTNET),
                ),
            ),
        )
    ),
}

data class RpcPreference(
    val providerId: RpcProviderId,
    val networkType: NetworkType,
)

enum class EvmChain(val chain: String) {
    ETHEREUM("eth"),
    BASE("base"),
    //LINEA("linea"),
    //ARBITRUM("arbitrum"),
    //OPTIMISM("optimism")
}
