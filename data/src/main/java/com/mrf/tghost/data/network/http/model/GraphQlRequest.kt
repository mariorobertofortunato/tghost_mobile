package com.mrf.tghost.data.network.http.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class GraphQlRequest(
    @SerialName("query") val query: String,
    @SerialName("variables") val variables: JsonObject? = null,
    @SerialName("operationName") val operationName: String? = null
)
