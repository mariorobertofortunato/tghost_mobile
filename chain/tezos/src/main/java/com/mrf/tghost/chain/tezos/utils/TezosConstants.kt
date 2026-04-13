package com.mrf.tghost.chain.tezos.utils

import java.math.BigDecimal

const val POLL_MS = 60_000L

val TEZOS_DECIMALS = BigDecimal(1_000_000)
const val TEZOS_NATIVE_XTZ_ACCOUNT = "tezos_native_account"

const val TEZOS_MAINNET_RPC_URL = "https://rpc.tzkt.io/mainnet/"
// ECAD Infra Tezos
const val TEZOS_MAINNET_RPC_URL_ECAD_INFRA = "https://mainnet.tezos.ecadinfra.com"
const val TEZOS_GHOSTNET_RPC_URL_ECAD_INFRA = "https://ghostnet.tezos.ecadinfra.com"

const val TZKT_API_URL = "https://api.tzkt.io/v1"

const val TZKT_EVENTS_URL = "https://api.tzkt.io/v1/events"