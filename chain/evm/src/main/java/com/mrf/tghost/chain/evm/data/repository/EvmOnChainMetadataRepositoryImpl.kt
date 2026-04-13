package com.mrf.tghost.chain.evm.data.repository

import com.mrf.tghost.data.network.http.factory.RpcRequestFactory
import com.mrf.tghost.domain.model.EvmChain
import com.mrf.tghost.domain.model.Result
import com.mrf.tghost.chain.evm.data.network.http.rpcrequests.getEvmTokenOnChainMetadataRequest
import com.mrf.tghost.chain.evm.data.network.mappers.toDomainModel
import com.mrf.tghost.chain.evm.data.network.model.alchemy.EvmMetadataResponseDto
import com.mrf.tghost.chain.evm.data.network.resolver.http.EvmHttpResolver
import com.mrf.tghost.chain.evm.domain.model.EvmMetadataResponse
import com.mrf.tghost.chain.evm.domain.repository.EvmOnChainMetadataRepository
import com.solana.rpccore.Rpc20Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class EvmOnChainMetadataRepositoryImpl @Inject constructor(
    private val evmHttpResolver: EvmHttpResolver
) : EvmOnChainMetadataRepository {

    override fun getEvmTokenOnChainMetadata(
        address: String,
        evmChain: EvmChain
    ): Flow<Result<EvmMetadataResponse>> = flow {
        emit(Result.Loading)
        val url = evmHttpResolver.resolveEvmUrl(evmChain)
        val response: Rpc20Response<EvmMetadataResponseDto> =
            RpcRequestFactory.makeRpcRequest(
                url = url,
                request = getEvmTokenOnChainMetadataRequest(address),
                resultSerializer = EvmMetadataResponseDto.serializer()
            )

        if (response.error != null) {
            emit(Result.Failure("${response.error?.code}, ${response.error?.message}"))
        }

        val alchemyResult = response.result
        if (alchemyResult != null) {
            emit(Result.Success(alchemyResult.toDomainModel()))
        } else {
            emit(Result.Failure(null))
        }
    }


}