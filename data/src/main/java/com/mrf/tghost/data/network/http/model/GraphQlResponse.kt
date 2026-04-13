package com.mrf.tghost.data.network.http.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Serializable
data class GraphQlResponse<T>(
    @SerialName("data") val data: T? = null,
    @SerialName("errors") val errors: List<GraphQlError>? = null,
    @SerialName("extensions") val extensions: JsonObject? = null
)

@Serializable
data class GraphQlError(
    @SerialName("message") val message: String,
    @SerialName("path") val path: List<JsonElement>? = null
)
