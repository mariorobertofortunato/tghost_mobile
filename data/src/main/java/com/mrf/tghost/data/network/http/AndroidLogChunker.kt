package com.mrf.tghost.data.network.http

import android.util.Log

/**
 * Logcat truncates single log lines (~4KB). Long HTTP bodies need to be split.
 */
internal object AndroidLogChunker {

    private const val MAX_CHUNK = 3500

    fun d(tag: String, message: String) {
        if (message.length <= MAX_CHUNK) {
            Log.d(tag, message)
            return
        }
        val chunks = message.chunked(MAX_CHUNK)
        val total = chunks.size
        chunks.forEachIndexed { index, chunk ->
            Log.d(tag, "[${index + 1}/$total] $chunk")
        }
    }
}
