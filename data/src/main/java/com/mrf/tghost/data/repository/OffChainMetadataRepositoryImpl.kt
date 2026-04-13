package com.mrf.tghost.data.repository

import com.mrf.tghost.data.network.http.factory.HttpRequestFactory
import com.mrf.tghost.data.network.model.metadata.TokenOffChainMetadataDto
import com.mrf.tghost.data.network.mappers.toDomainModel
import com.mrf.tghost.domain.model.Result
import com.mrf.tghost.domain.model.metadata.TokenOffChainMetadata
import com.mrf.tghost.domain.repository.OffChainMetadataRepository
import io.ktor.client.call.body
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class OffChainMetadataRepositoryImpl @Inject constructor() : OffChainMetadataRepository {
    override fun getOffChainMetadata(url: String): Flow<Result<TokenOffChainMetadata?>> = flow {
        emit(Result.Loading)
        if (url.isBlank()) {
            emit(Result.Success(null))
            return@flow
        }
        val response = HttpRequestFactory.makeHttpRequest(url)
        if (response is Result.Success) {
            val httpResponse = response.data
            val contentType = httpResponse?.headers?.get("Content-Type")?.lowercase()
            if (contentType != null && contentType.startsWith("image/")) {
                emit(
                    Result.Success(
                        TokenOffChainMetadata(
                            image = url,
                            name = "Unknown",
                            description = "No metadata found"
                        )
                    )
                )
            } else {
                val metadataResponse = response.data?.body<TokenOffChainMetadataDto>()
                emit(Result.Success(metadataResponse?.toDomainModel()))
            }
        }
        if (response is Result.Failure) {
            emit(Result.Failure(response.errorMessage))
        }
    }
}
