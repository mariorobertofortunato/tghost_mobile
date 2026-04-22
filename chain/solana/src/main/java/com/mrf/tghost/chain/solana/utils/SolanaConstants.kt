package com.mrf.tghost.chain.solana.utils

import java.math.BigDecimal

const val POLL_MS = 60_000L

val LAMPORTS_IN_SOL = BigDecimal(1_000_000_000)
const val SOLANA_TOKEN_MINT = "So11111111111111111111111111111111111111112"
const val SOLANA_STAKE_PROGRAM_ID = "Stake11111111111111111111111111111111111111"
const val SOLANA_SPL_TOKEN_PROGRAM_ID = "TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA"
const val METADATA_PROGRAM_ID_STRING_FOR_TMU = "metaqbxxUerdq28cj1RbAWkYQm3ybzjb6a8bt518x1s"

/** RPCs **/
const val SOLANA_MAINNET_RPC_URL = "https://api.mainnet.solana.com/"
const val SOLANA_TESTNET_RPC_URL = "https://api.testnet.solana.com/"
const val SOLANA_DEVNET_RPC_URL = "https://api.devnet.solana.com/"
// Helius
const val SOLANA_MAINNET_RPC_URL_HELIUS = "https://mainnet.helius-rpc.com/"
const val SOLANA_TESTNET_RPC_URL_HELIUS = "https://testnet.helius-rpc.com/"
const val SOLANA_DEVNET_RPC_URL_HELIUS = "https://devnet.helius-rpc.com/"
// dRPC Solana (public mainnet endpoint)
const val SOLANA_MAINNET_RPC_URL_DRPC = "https://solana.drpc.org"
// PublicNode HTTP endpoint
const val SOLANA_MAINNET_RPC_URL_PUBLIC_NODE = "https://solana-rpc.publicnode.com"


// ------------------------- SOLANA WS (Official)
const val SOLANA_MAINNET_WS_URL = "wss://api.mainnet-beta.solana.com"
const val SOLANA_TESTNET_WS_URL = "wss://api.testnet.solana.com"
const val SOLANA_DEVNET_WS_URL = "wss://api.devnet.solana.com"
// Helius WS
const val SOLANA_MAINNET_WS_URL_HELIUS = "wss://mainnet.helius-rpc.com/"
const val SOLANA_TESTNET_WS_URL_HELIUS = "wss://testnet.helius-rpc.com/"
const val SOLANA_DEVNET_WS_URL_HELIUS = "wss://devnet.helius-rpc.com/"
// dRPC Solana WS
const val SOLANA_MAINNET_WS_URL_DRPC_BASE = "wss://lb.drpc.live/solana/"
// PublicNode WS
const val SOLANA_MAINNET_WS_URL_PUBLIC_NODE = "wss://solana-rpc.publicnode.com"