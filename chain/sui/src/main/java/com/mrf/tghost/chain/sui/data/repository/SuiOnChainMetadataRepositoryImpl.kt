package com.mrf.tghost.chain.sui.data.repository

import com.mrf.tghost.chain.sui.data.network.http.rpcrequests.getSuiCoinMetadataGraphQlRequest
import com.mrf.tghost.chain.sui.data.network.mappers.toDomainModel
import com.mrf.tghost.chain.sui.data.network.model.SuiCoinMetadataGraphQlDataDto
import com.mrf.tghost.chain.sui.data.network.resolver.http.SuiHttpResolver
import com.mrf.tghost.chain.sui.domain.model.SuiCoinMetadata
import com.mrf.tghost.chain.sui.domain.repository.SuiOnChainMetadataRepository
import com.mrf.tghost.data.network.http.factory.GraphQlRequestFactory
import com.mrf.tghost.domain.model.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SuiOnChainMetadataRepositoryImpl @Inject constructor(
    private val suiHttpResolver: SuiHttpResolver,
) : SuiOnChainMetadataRepository {

    override fun getSuiCoinMetadata(coinType: String): Flow<Result<SuiCoinMetadata?>> = flow {
        emit(Result.Loading)
        val response = GraphQlRequestFactory.makeGraphQlRequest(
            url = suiHttpResolver.resolveSuiGraphQlUrl(),
            request = getSuiCoinMetadataGraphQlRequest(coinType),
            dataSerializer = SuiCoinMetadataGraphQlDataDto.serializer()
        )

        val errors = response.errors
        if (!errors.isNullOrEmpty()) {
            emit(Result.Failure(errors.joinToString("; ") { it.message }))
            return@flow
        }

        val node = response.data?.coinMetadata
        if (node == null) {
            emit(Result.Failure("No metadata found for $coinType"))
        } else {
            emit(Result.Success(node.toDomainModel()))
        }
    }
}
