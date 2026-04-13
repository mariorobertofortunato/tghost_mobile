package com.mrf.tghost.data.network.http.factory

import android.util.Log
import com.mrf.tghost.data.network.client.KtorClient
import com.mrf.tghost.data.network.http.model.GraphQlError
import com.mrf.tghost.data.network.http.model.GraphQlRequest
import com.mrf.tghost.data.network.http.model.GraphQlResponse
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import java.io.IOException

object GraphQlRequestFactory {

    private const val TAG = "GraphQlRequestFactory"

    private val jsonConfig = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    suspend fun <T> makeGraphQlRequest(
        url: String,
        request: GraphQlRequest,
        dataSerializer: KSerializer<T>
    ): GraphQlResponse<T> =
        withContext(Dispatchers.IO) {
            try {
                val responseText = KtorClient.httpClient.post(url) {
                    contentType(ContentType.Application.Json)
                    setBody(request)
                }.bodyAsText()

                jsonConfig.decodeFromString(
                    GraphQlResponse.serializer(dataSerializer),
                    responseText
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                Log.w(TAG, "[$url] gql request failed (IO)", e)
                GraphQlResponse(errors = listOf(GraphQlError(e.message ?: e.toString())))
            } catch (e: Exception) {
                Log.w(TAG, "[$url] gql request failed", e)
                GraphQlResponse(errors = listOf(GraphQlError(e.message ?: e.toString())))
            }
        }
}
