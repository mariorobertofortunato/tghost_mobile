package com.mrf.tghost.domain.network

import kotlinx.coroutines.flow.Flow

interface WebSocketManager {

    /** Disconnects all active connections. */
    fun disconnectAll()

    /**
     * Connects to the given WebSocket URL. Used by chain modules that resolve their own URL.
     */
    suspend fun connectByUrl(url: String)

    suspend fun sendByUrl(url: String, text: String)

    suspend fun messageFlowByUrl(url: String): Flow<String>

    suspend fun disconnectByUrl(url: String)
}
