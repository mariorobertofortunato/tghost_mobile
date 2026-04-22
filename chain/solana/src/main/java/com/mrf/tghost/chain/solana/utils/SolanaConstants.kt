package com.mrf.tghost.chain.solana.utils

import java.math.BigDecimal

const val POLL_MS = 60_000L

val LAMPORTS_IN_SOL = BigDecimal(1_000_000_000)
const val SOLANA_TOKEN_MINT = "So11111111111111111111111111111111111111112"
const val SOLANA_STAKE_PROGRAM_ID = "Stake11111111111111111111111111111111111111"
const val SOLANA_SPL_TOKEN_PROGRAM_ID = "TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA"
const val METADATA_PROGRAM_ID_STRING_FOR_TMU = "metaqbxxUerdq28cj1RbAWkYQm3ybzjb6a8bt518x1s"
const val SOLANA_JUPITER_V6_PROGRAM_ID = "JUP6LkbZbjS1jKKwapdHNy74zcZ3tLUZoi5QNyVTaV4"
const val SOLANA_RAYDIUM_AMM_V4_PROGRAM_ID = "675kPX9MHTjS2zt1qfr1NYHuzefx9Q6mP6fQanMjt6v"
const val SOLANA_ORCA_WHIRLPOOL_PROGRAM_ID = "whirLbMiicVdio4qvUfM5KAg6Ct8VwpYzGff3uctyCc"

val SOLANA_SWAP_PROGRAM_IDS = setOf(
    SOLANA_JUPITER_V6_PROGRAM_ID,
    SOLANA_RAYDIUM_AMM_V4_PROGRAM_ID,
    SOLANA_ORCA_WHIRLPOOL_PROGRAM_ID,
    SOLANA_SPL_TOKEN_PROGRAM_ID
)

val SOLANA_KNOWN_MINT_SYMBOLS = mapOf(
    // Native wrappers and major stablecoins
    "So11111111111111111111111111111111111111112" to "SOL",
    "EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v" to "USDC",
    "Es9vMFrzaCERmJfrF4H2FYD4KCoNkY11McCe8BenwNYB" to "USDT",
    // Common ecosystem tokens
    "JUPyiwrYJFskUPiHa7hkeR8VUtAeFoSYbKedZNsDvCN" to "JUP",
    "jtojtomepa8beP8AuQc6eXt5FriJwfFMwQx2v2f9mCL" to "JTO",
    "DezXAZ8z7PnrnRJjz3wXBoRgixCa6xjnB7YaB1pPB263" to "BONK",
    "EKpQGSJtjMFqKZ9KQanSqYXRcF8fBopzLHYxdM65zcjm" to "WIF",
    "HZ1JovNiVvGrGNiiYvEozEVgZ58xaU3RKwX8eACQBCt3" to "PYTH",
    "4k3Dyjzvzp8eMZWUXbBCjEvwSkkk59S5iCNLY3QrkX6R" to "RAY",
    "mSoLzYCxAfYgP4Ze3tXRvLWq2JzPyS5u2poU43Q1Lv1" to "mSOL",
    "bSo13r4TkiE4KumL71LsHvpL2ro2TqGzKqCfmTfqTzU" to "bSOL",
    "27G8iuvjN5yYCJTfUXHAeGxbRg4T585yP8c4e694J3yP" to "JLP",
    "5oVNBeEEQvYi1cX3ir8Dx5n1P7pdxodbGF2X4CfgVusJ" to "INF"
)

/** RPCs **/
const val SOLANA_MAINNET_RPC_URL = "https://api.mainnet.solana.com/"
const val SOLANA_TESTNET_RPC_URL = "https://api.testnet.solana.com/"
const val SOLANA_DEVNET_RPC_URL = "https://api.devnet.solana.com/"
// Helius
const val SOLANA_MAINNET_RPC_URL_HELIUS = "https://mainnet.helius-rpc.com/"
const val SOLANA_TESTNET_RPC_URL_HELIUS = "https://testnet.helius-rpc.com/"
const val SOLANA_DEVNET_RPC_URL_HELIUS = "https://devnet.helius-rpc.com/"
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
// PublicNode WS
const val SOLANA_MAINNET_WS_URL_PUBLIC_NODE = "wss://solana-rpc.publicnode.com"