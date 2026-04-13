package com.mrf.tghost.chain.evm.data.network.resolver.websocket

import com.mrf.tghost.domain.repository.PreferencesRepository
import javax.inject.Inject

class EvmWsResolver @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
) {

    fun resolveEvmWsUrl(): String? {
        // TODO: map EVM providers to WebSocket URLs when needed
        return null
    }


}