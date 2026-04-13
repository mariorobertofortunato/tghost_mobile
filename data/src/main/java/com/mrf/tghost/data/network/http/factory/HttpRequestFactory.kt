package com.mrf.tghost.data.network.http.factory

import com.mrf.tghost.data.network.client.KtorClient
import com.mrf.tghost.domain.model.Result
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.UnknownHostException

object HttpRequestFactory {

    private const val TAG = "HttpRequestFactory"

    suspend fun makeHttpRequest(url: String): Result<HttpResponse?> {

        if (url.isBlank()) {
            return Result.Failure("// $TAG URI null or empty.")
        }
        val validUrl = try {
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                return Result.Failure("// $TAG URL not valid (non HTTP/HTTPS): $url")
            } else
                url
        } catch (e: Exception) {
            return Result.Failure("// $TAG URL not valid (non HTTP/HTTPS): $url, Exception: $e")
        }
        return withContext(Dispatchers.IO) {
            try {
                val response = KtorClient.httpClient.get(validUrl)
                if (!response.status.isSuccess()) {
                    return@withContext Result.Failure("// $TAG Failed get response from: $validUrl - Status: ${response.status} - Text: ${response.status.description}")
                } else {
                    return@withContext Result.Success(response)
                }
            } catch (e: NoTransformationFoundException) {
                return@withContext Result.Failure("// $TAG NoTransformationFoundException - Exception: ${e.message}")
            } catch (e: HttpRequestTimeoutException) {
                return@withContext Result.Failure("// $TAG HttpRequestTimeoutException - Exception: ${e.message}")
            } catch (e: UnknownHostException) {
                return@withContext Result.Failure("// $TAG UnknownHostException - Exception: ${e.message}")
            } catch (e: Exception) {
                return@withContext Result.Failure("// $TAG Generic Exception - Exception: ${e.message}")
            }
        }
    }

}