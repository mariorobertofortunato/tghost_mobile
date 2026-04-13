package com.mrf.tghost.data.utils

// DATASTORE
const val USER_PREFERENCES_DATASTORE_KEY = "user_preferences"
const val ONBOARDING_STATUS_KEY = "onboarding_status"
const val LIVE_UPDATE_STATUS_KEY = "live_update_status"
// RPC preference: provider id + network type
const val SOLANA_RPC_PROVIDER_ID_KEY = "solana_rpc_provider_id"
const val SOLANA_RPC_NETWORK_TYPE_KEY = "solana_rpc_network_type"
const val EVM_RPC_PROVIDER_ID_KEY = "evm_rpc_provider_id"
const val EVM_RPC_NETWORK_TYPE_KEY = "evm_rpc_network_type"
const val SUI_RPC_PROVIDER_ID_KEY = "sui_rpc_provider_id"
const val SUI_RPC_NETWORK_TYPE_KEY = "sui_rpc_network_type"
const val TEZOS_RPC_PROVIDER_ID_KEY = "tezos_rpc_provider_id"
const val TEZOS_RPC_NETWORK_TYPE_KEY = "tezos_rpc_network_type"
const val RPC_API_KEY_PREFIX = "rpc_api_key_" // Key name = RPC_API_KEY_PREFIX + providerId.name.lowercase()

// MARKET DATA INDEXER / AGGREGATOR
const val MARKET_DATA_URL_DEXSCREENER = "https://api.dexscreener.com/tokens/v1/"
const val TZKT_API_URL = "https://api.tzkt.io/v1" // Tezos specific market data url




