package com.mrf.tghost.chain.evm.data.repository

import com.mrf.tghost.data.network.http.factory.RpcRequestFactory
import com.mrf.tghost.domain.model.EvmChain
import com.mrf.tghost.domain.model.Result
import com.mrf.tghost.chain.evm.data.network.http.rpcrequests.getEvmTokenAccountsRequest
import com.mrf.tghost.chain.evm.data.network.model.alchemy.EvmTokenBalancesDto
import com.mrf.tghost.chain.evm.data.network.resolver.http.EvmHttpResolver
import com.mrf.tghost.chain.evm.domain.model.EvmTokenAccount
import com.mrf.tghost.chain.evm.domain.repository.EvmTokenAccountsRepository
import com.solana.rpccore.Rpc20Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.math.BigInteger
import javax.inject.Inject

class EvmTokenAccountsRepositoryImpl @Inject constructor(
    private val evmHttpResolver: EvmHttpResolver
) : EvmTokenAccountsRepository {

    override fun evmTokenAccounts(publicKey: String, chainId: EvmChain): Flow<Result<List<EvmTokenAccount>>?> = flow {
        val httpResult = getEvmTokenAccountsHttp(publicKey, chainId)
        emit(httpResult)
        // todo websocket/polling etc
    }.flowOn(Dispatchers.IO)

    private suspend fun getEvmTokenAccountsHttp(address: String, chainId: EvmChain): Result<List<EvmTokenAccount>> =
        withContext(Dispatchers.IO) {
            val url = evmHttpResolver.resolveEvmUrl(chainId)
            val response: Rpc20Response<EvmTokenBalancesDto> =
                RpcRequestFactory.makeRpcRequest(
                    url = url,
                    request = getEvmTokenAccountsRequest(address),
                    resultSerializer = EvmTokenBalancesDto.serializer()
                )

            if (response.error != null) {
                return@withContext Result.Failure("${response.error?.code}, ${response.error?.message}")
            }

            val alchemyResult = response.result
            if (alchemyResult != null) {
                val domainList = alchemyResult.tokenBalances.map { token ->
                    val hexBalance = token.tokenBalance
                    val decimalBalance = try {
                        if (!hexBalance.isNullOrEmpty() && hexBalance.startsWith("0x")) {
                            BigInteger(hexBalance.substring(2), 16).toString()
                        } else {
                            hexBalance
                        }
                    } catch (e: Exception) {
                        "0"
                    }

                    EvmTokenAccount(
                        contractAddress = token.contractAddress,
                        balance = decimalBalance?.toBigDecimal() ?: BigInteger.ZERO.toBigDecimal(),
                        rawBalance = decimalBalance ?: "0",
                        name = null, // Will be fetched later
                        symbol = null, // Will be fetched later
                        decimals = null // Will be fetched later
                    )
                }
                Result.Success(domainList)
            } else {
                Result.Success(emptyList())
            }
        }
}