package com.mrf.tghost.data.network.client

import com.mrf.tghost.data.network.http.AndroidLogChunker
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 *
 * Common client used by http and websocket
 *
 * */
object KtorClient {
    val httpClient: HttpClient =
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                val jsonConfig = Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                    encodeDefaults = true
                }
                json(jsonConfig, ContentType.Application.Json)
                json(jsonConfig, ContentType.Text.Plain)
                json(jsonConfig, ContentType.Any)
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        AndroidLogChunker.d("KtorClient", message)
                    }
                }
                level = LogLevel.BODY
            }
            // WebSocket support
            install(WebSockets)
        }
}
