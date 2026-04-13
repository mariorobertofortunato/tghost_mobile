package com.mrf.tghost.data.network.websocket.core

import android.util.Log
import com.mrf.tghost.data.network.client.KtorClient
import com.mrf.tghost.domain.network.WebSocketManager
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "WebSocketManagerImpl"
private const val RECONNECT_DELAY_MS = 2_000L
private const val MAX_RECONNECT_DELAY_MS = 60_000L

private const val WS_REPLAY_CACHE_SIZE = 8

@Singleton
class WebSocketManagerImpl @Inject constructor() : WebSocketManager {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val connections = ConcurrentHashMap<String, ConnectionState>()

    override fun disconnectAll() {
        connections.keys.toList().forEach { disconnectByUrlInternal(it) }
    }

    override suspend fun connectByUrl(url: String) {
        if (connections[url]?.job?.isActive == true) return
        val existingState = connections[url]
        existingState?.job?.cancel()
        val incomingFlow = existingState?.incomingFlow
            ?: MutableSharedFlow<String>(replay = WS_REPLAY_CACHE_SIZE, extraBufferCapacity = 128)
        val sendChannel = Channel<String>(Channel.UNLIMITED)
        val job = scope.launch {
            runConnection(url, incomingFlow, sendChannel)
        }
        connections[url] = ConnectionState(
            job = job,
            sendChannel = sendChannel,
            incomingFlow = incomingFlow
        )
    }

    override suspend fun sendByUrl(url: String, text: String) {
        val channel = connections[url]?.sendChannel
        if (channel == null) {
            Log.w(TAG, "[$url] sendByUrl: no connection state")
            return
        }
        val preview = if (text.length <= 160) text else text.take(157) + "..."
        Log.d(TAG, "[$url] sendByUrl <<< len=${text.length} text=$preview")
        channel.trySend(text)
    }

    override suspend fun messageFlowByUrl(url: String): Flow<String> {
        val state = connections[url] ?: return emptyFlow()
        return state.incomingFlow.asSharedFlow()
    }

    override suspend fun disconnectByUrl(url: String) {
        disconnectByUrlInternal(url)
    }

    private fun disconnectByUrlInternal(url: String) {
        val state = connections.remove(url) ?: return
        state.job.cancel()
        state.sendChannel.close()
    }

    private suspend fun runConnection(
        url: String,
        incomingFlow: MutableSharedFlow<String>,
        sendChannel: Channel<String>
    ) {
        var delayMs = RECONNECT_DELAY_MS
        while (scope.isActive) {
            try {
                KtorClient.httpClient.webSocket(urlString = url) wsSession@ {
                    delayMs = RECONNECT_DELAY_MS
                    coroutineScope {
                        launch { runSendLoop(this@wsSession, sendChannel, url) }
                        launch { runReceiveLoop(this@wsSession, incomingFlow, url) }
                    }
                }
            } catch (e: Exception) {
                if (!scope.isActive) break
                Log.w(TAG, "[$url] connection error: ${e.message}")
            }
            if (!scope.isActive) break
            delay(delayMs)
            delayMs = (delayMs * 2).coerceAtMost(MAX_RECONNECT_DELAY_MS)
        }
    }

    private suspend fun runSendLoop(
        session: DefaultClientWebSocketSession,
        sendChannel: Channel<String>,
        url: String
    ) {
        try {
            for (text in sendChannel) {
                session.outgoing.send(Frame.Text(text))
            }
        } catch (e: Exception) {
            Log.w(TAG, "[$url] runSendLoop ended: ${e.message}")
        }
    }

    private suspend fun runReceiveLoop(
        session: DefaultClientWebSocketSession,
        incomingFlow: MutableSharedFlow<String>,
        url: String
    ) {
        try {
            for (frame in session.incoming) {
                when (frame) {
                    is Frame.Text -> {
                        val text = frame.readText()
                        val preview = if (text.length <= 160) text else text.take(157) + "..."
                        Log.d(TAG, "[$url] recv >>> len=${text.length} text=$preview")
                        if (!incomingFlow.tryEmit(text)) Log.w(TAG, "[$url] tryEmit failed (buffer full)")
                    }
                    is Frame.Close -> session.close()
                    else -> { }
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "[$url] runReceiveLoop ended: ${e.message}")
        }
    }

    private data class ConnectionState(
        val job: Job,
        val sendChannel: Channel<String>,
        val incomingFlow: MutableSharedFlow<String>
    )
}
