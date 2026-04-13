package com.mrf.tghost.chain.sui.data.repository

import com.mrf.tghost.chain.sui.data.network.http.rpcrequests.getSuiStakesRequest
import com.mrf.tghost.chain.sui.data.network.mappers.toSuiStakes
import com.mrf.tghost.chain.sui.data.network.model.SuiStakesDto
import com.mrf.tghost.chain.sui.data.network.resolver.http.SuiHttpResolver
import com.mrf.tghost.chain.sui.domain.model.SuiStake
import com.mrf.tghost.chain.sui.domain.repository.SuiStakingRepository
import com.mrf.tghost.data.network.http.factory.RpcRequestFactory
import com.mrf.tghost.domain.model.Result
import com.solana.rpccore.Rpc20Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.ListSerializer
import javax.inject.Inject

class SuiStakingRepositoryImpl @Inject constructor(
    private val suiHttpResolver: SuiHttpResolver,
) : SuiStakingRepository {

    override fun suiStakingAccounts(publicKey: String): Flow<Result<List<SuiStake>>?> = flow<Result<List<SuiStake>>?> {
        val httpResult = getSuiStakingAccountsHttp(publicKey)
        emit(httpResult)
        // TODO polling/websocket
    }.flowOn(Dispatchers.IO)

    private suspend fun getSuiStakingAccountsHttp(walletAddress: String): Result<List<SuiStake>> =
        withContext(Dispatchers.IO) {
            val url = suiHttpResolver.resolveSuiUrl()
            val response: Rpc20Response<List<SuiStakesDto>> = RpcRequestFactory.makeRpcRequest(
                url = url,
                request = getSuiStakesRequest(walletAddress),
                resultSerializer = ListSerializer(SuiStakesDto.serializer())
            )

            if (response.error != null) {
                Result.Failure("${response.error?.code}, ${response.error?.message}")
            } else {
                val stakes = response.result ?: emptyList()
                Result.Success(stakes.flatMap { it.toSuiStakes() })
            }
        }

}
