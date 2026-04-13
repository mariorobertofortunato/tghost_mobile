package com.mrf.tghost.data.network.http.factory

import android.util.Log
import com.mrf.tghost.data.network.http.KtorHttpDriver
import com.solana.networking.Rpc20Driver
import com.solana.rpccore.JsonRpc20Request
import com.solana.rpccore.Rpc20Response
import com.solana.rpccore.RpcError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap

object RpcRequestFactory {

    private const val TAG = "RpcRequestFactory"

    /** JSON-RPC 2.0: internal error (transport / unexpected failure). */
    private const val JSON_RPC_INTERNAL_ERROR = -32603

    private val driversCache = ConcurrentHashMap<String, Rpc20Driver>()

    suspend fun <T> makeRpcRequest(
        url: String,
        request: JsonRpc20Request,
        resultSerializer: KSerializer<T>
    ): Rpc20Response<T> =
        withContext(Dispatchers.IO) {
            try {
                val rpcDriver = getOrCreateDriver(url)
                val response = rpcDriver.makeRequest(request, resultSerializer)
                Log.d(TAG, "[$url] req: ${request.method} -> res: ${response.result.toString()}")
                response
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                Log.w(TAG, "[$url] req: ${request.method} failed (IO)", e)
                Rpc20Response(
                    result = null,
                    error = RpcError(JSON_RPC_INTERNAL_ERROR, e.message ?: e.toString())
                )
            } catch (e: Exception) {
                Log.w(TAG, "[$url] req: ${request.method} failed", e)
                Rpc20Response(
                    result = null,
                    error = RpcError(JSON_RPC_INTERNAL_ERROR, e.message ?: e.toString())
                )
            }
        }

    private fun getOrCreateDriver(url: String): Rpc20Driver {
        return driversCache.computeIfAbsent(url) { newUrl ->
            Log.d(TAG, "Creating new Rpc20Driver for: $newUrl")
            Rpc20Driver(newUrl, KtorHttpDriver())
        }
    }
}