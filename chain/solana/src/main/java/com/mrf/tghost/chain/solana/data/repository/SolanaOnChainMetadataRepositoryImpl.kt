package com.mrf.tghost.chain.solana.data.repository

import android.util.Base64
import com.mrf.tghost.chain.solana.data.network.http.rpcrequests.getSolanaAccountInfoRequest
import com.mrf.tghost.chain.solana.data.network.model.SolanaAccountInfoDto
import com.mrf.tghost.chain.solana.data.network.model.SolanaRpcResponseDto
import com.mrf.tghost.chain.solana.data.network.resolver.http.SolanaHttpResolver
import com.mrf.tghost.chain.solana.domain.repository.SolanaOnChainMetadataRepository
import com.mrf.tghost.data.network.http.factory.RpcRequestFactory
import com.mrf.tghost.data.network.mappers.mapOnChainMetadata
import com.mrf.tghost.domain.model.Result
import com.mrf.tghost.domain.model.metadata.TokenOnChainMetadata
import com.solana.rpccore.Rpc20Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.builtins.nullable
import javax.inject.Inject

class SolanaOnChainMetadataRepositoryImpl @Inject constructor(
    private val solanaHttpResolver: SolanaHttpResolver,
) : SolanaOnChainMetadataRepository {

    override fun getSolanaTokenOnChainMetadata(metadataPDAString: String): Flow<Result<TokenOnChainMetadata?>> = flow {
        emit(Result.Loading)
        val url = solanaHttpResolver.resolveSolanaUrl()
        val response: Rpc20Response<SolanaRpcResponseDto<SolanaAccountInfoDto?>> =
            RpcRequestFactory.makeRpcRequest(
                url = url,
                request = getSolanaAccountInfoRequest(metadataPDAString),
                resultSerializer = SolanaRpcResponseDto.serializer(SolanaAccountInfoDto.serializer().nullable)
            )
        response.error?.let { error ->
            emit(Result.Failure("${error.code}, ${error.message}"))
        }
        response.result?.value.let { accountInfoValue ->
            if (accountInfoValue == null) {
                emit(Result.Failure("[ON_CHAIN_METADATA_USE_CASE] No account found at PDA: $metadataPDAString"))
                return@flow
            }
            if (accountInfoValue.data.size == 2 && accountInfoValue.data[1] == "base64") {
                val base64Data = accountInfoValue.data[0]
                if (base64Data.isNotEmpty()) {
                    val accountDataBytes = Base64.decode(base64Data, Base64.DEFAULT)
                    emit(Result.Success(mapOnChainMetadata(accountDataBytes)))
                } else {
                    emit(Result.Failure("[ON_CHAIN_METADATA_USE_CASE] Base64 is empty: $base64Data"))
                }
            } else {
                emit(Result.Failure("[ON_CHAIN_METADATA_USE_CASE] Not Base64: ${accountInfoValue.data}"))
            }
        }
    }.flowOn(Dispatchers.IO)

}
