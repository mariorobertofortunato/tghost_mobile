package com.mrf.tghost.chain.evm.processor

import com.mrf.tghost.chain.evm.domain.usecase.GetNftUseCase
import com.mrf.tghost.chain.evm.domain.usecase.GetOnChainMetadataUseCase
import com.mrf.tghost.chain.evm.domain.usecase.GetStakingAccountsUseCase
import com.mrf.tghost.chain.evm.domain.usecase.GetTokenAccountsUseCase
import com.mrf.tghost.chain.evm.domain.usecase.GetWalletBalanceUseCase
import com.mrf.tghost.chain.evm.processor.EvmProcessorHelper.isValidNft
import com.mrf.tghost.data.utils.MARKET_DATA_URL_DEXSCREENER
import com.mrf.tghost.domain.model.EvmChain
import com.mrf.tghost.domain.model.SupportedChain
import com.mrf.tghost.domain.model.TokenAccount
import com.mrf.tghost.domain.model.TokenMarketDataInfo
import com.mrf.tghost.domain.model.WalletUpdate
import com.mrf.tghost.domain.model.isSuccess
import com.mrf.tghost.domain.model.isFailure
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.lastOrNull
import java.math.BigDecimal
import javax.inject.Inject
import com.mrf.tghost.domain.model.Result
import com.mrf.tghost.domain.usecase.GetMarketDataUseCase
import com.mrf.tghost.chain.evm.processor.EvmProcessorHelper.nativeMintFor
import kotlin.collections.forEach

class EvmProcessor @Inject constructor (
    private val walletBalanceUseCase: GetWalletBalanceUseCase,
    private val tokenAccountsUseCase: GetTokenAccountsUseCase,
    private val stakingAccountsUseCase: GetStakingAccountsUseCase,
    private val nftUseCase: GetNftUseCase,
    private val onChainMetadataUseCase: GetOnChainMetadataUseCase,
    private val getMarketDataUseCase: GetMarketDataUseCase
){

    fun processEvmWallet(publicKey: String) = flow {
        emit(WalletUpdate.LoadingStage("// Loading Evm wallet…"))

        val allTokens = mutableListOf<TokenAccount>()
        var totalUsd = 0.0
        var totalNative = 0.0

        for (chain in EvmChain.entries) {
            processEvmChain(chain, publicKey).collect { update ->
                when (update) {
                    is EvmChainUpdate.LoadingStage -> emit(WalletUpdate.LoadingStage(update.stage))
                    is EvmChainUpdate.Success -> {
                        totalUsd += update.totalUsd
                        totalNative += update.totalNative
                        allTokens.addAll(update.tokens)
                    }
                    is EvmChainUpdate.Error -> {
                        emit(WalletUpdate.Error(update.message))
                        return@collect
                    }
                }
            }
        }
        emit(WalletUpdate.Success(totalUsd, totalNative, allTokens))
    }.catch {
        emit(WalletUpdate.Error(it.message ?: it.cause?.message ?: "Unknown error"))
    }

    /**
     * Processes a single EVM chain and emits loading stages plus a final Success or Error.
     */
    private fun processEvmChain(evmChainId: EvmChain, publicKey: String): Flow<EvmChainUpdate> = flow {
        emit(EvmChainUpdate.LoadingStage("// ${evmChainId.name} processing started"))

        suspend fun getMarketData(address: String): Deferred<Result<TokenMarketDataInfo?>> {
            return coroutineScope {
                async {
                    getMarketDataUseCase.fetchMarketDataInfo(
                        MARKET_DATA_URL_DEXSCREENER,
                        address,
                        SupportedChain.EVM,
                        if (evmChainId.chain == "eth") "ethereum" else evmChainId.chain
                    ).last()
                }
            }
        }

        val balanceFlow = walletBalanceUseCase.balanceEvm(publicKey, evmChainId)
        val tokenAccountsFlow = tokenAccountsUseCase.evmTokenAccounts(publicKey, evmChainId)
        val stakingAccountsFlow = stakingAccountsUseCase.evmStakingAccounts(publicKey, evmChainId.chain)
        val nftFlow = nftUseCase.evmNFTSAccounts(publicKey, evmChainId.chain)

        combineTransform(
            balanceFlow,
            tokenAccountsFlow,
            stakingAccountsFlow,
            nftFlow
        ) { balanceFlow, tokenAccountsFlow, stakingAccountsFlow, nftsFlow ->
            if (balanceFlow == null || tokenAccountsFlow == null || stakingAccountsFlow == null || nftsFlow == null) {
                emit(EvmChainUpdate.LoadingStage("// Synchronizing ${evmChainId.name} data…"))
                return@combineTransform
            }

            val processedTokens = mutableListOf<TokenAccount>()
            var ethPriceUsdString = ""
            var ethPriceUsd = 0.0

            // 1. Native EVM token (e.g. ETH / Base ETH)
            emit(EvmChainUpdate.LoadingStage("// Processing native ${evmChainId.name} coins"))
            if (balanceFlow.isSuccess()) {
                val nativeAmount = balanceFlow.data
                val nativeQuote = getMarketData(nativeMintFor(evmChainId.chain)).await()
                if (nativeQuote.isSuccess()) {
                    val ethereumTokenAccount =
                        EvmProcessorHelper.createEthNativeTokenAccountItem(
                            quote = nativeQuote.data ?: TokenMarketDataInfo(),
                            balance = nativeAmount
                        )
                    processedTokens.add(ethereumTokenAccount)

                    ethPriceUsdString = nativeQuote.data?.priceUsd ?: "1"
                    ethPriceUsd = ethPriceUsdString.toDoubleOrNull() ?: 0.0
                }
            }
            else if (balanceFlow.isFailure()) {
                emit(EvmChainUpdate.Error("${evmChainId.name} balance: ${balanceFlow.errorMessage}"))
            }

            // 2. EVM Token Accounts
            emit(EvmChainUpdate.LoadingStage("// Processing ${evmChainId.name} Token Accounts"))
            if (tokenAccountsFlow.isSuccess()) {
                val tokenList = tokenAccountsFlow.data
                val tokenDetails = tokenList
                    .filter { it.balance > BigDecimal.ZERO }
                    .map { tokenAccount ->
                        coroutineScope {
                            async(Dispatchers.IO) {

                                val onChainMetadata = try {
                                    val res = onChainMetadataUseCase.getEvmTokenOnChainMetadata(
                                        address = tokenAccount.contractAddress,
                                        evmChain = evmChainId
                                    ).lastOrNull()
                                    if (res is Result.Success) res.data else null
                                } catch (e: Exception) {
                                    null
                                }

                                val marketData = getMarketData(tokenAccount.contractAddress).await()

                                val marketDataInfo = if (marketData.isSuccess()) {
                                    marketData.data
                                } else null

                                Triple(tokenAccount, onChainMetadata, marketDataInfo)
                            }
                        }
                    }
                    .awaitAll()
                tokenDetails
                    .filter { (_, _, marketDataInfo) -> (marketDataInfo?.priceUsdDouble ?: 0.0) > 0.001 }
                    .forEach { (tokenAccount, onChainMetadata, marketDataInfo) ->
                        val tokenItem =
                            EvmProcessorHelper.createEvmTokenAccountItem(
                                tokenAccountDetails = tokenAccount,
                                marketDataInfo = marketDataInfo,
                                onChainMetadata = onChainMetadata,
                                evmChainId = evmChainId.chain
                            )
                        processedTokens.add(tokenItem)
                    }
            }
            else if (tokenAccountsFlow.isFailure()) {
                emit(EvmChainUpdate.Error("${evmChainId.name} tokens: ${tokenAccountsFlow.errorMessage}"))
            }

            // 3. Staking Accounts
            emit(EvmChainUpdate.LoadingStage("// Processing ${evmChainId.name} Native Staking Accounts"))
            if (stakingAccountsFlow.isSuccess()) {
                val stakes = stakingAccountsFlow.data
                stakes.forEach { stakeAccount ->
                    val posBalance = stakeAccount.position?.balanceUsd ?: 0.0
                    if (posBalance > 0.0) {
                        processedTokens.add(
                            EvmProcessorHelper.createStakedEthAccountItem(
                                stakeAccount = stakeAccount,
                                stakedEthAmount = posBalance.toBigDecimal()
                            )
                        )
                    }
                }
            }
            else if (stakingAccountsFlow.isFailure()) {
                emit(EvmChainUpdate.Error("${evmChainId.name} Staking Accounts: ${stakingAccountsFlow.errorMessage}"))
            }

            // 4. EVM NFTs
            emit(EvmChainUpdate.LoadingStage("// Processing ${evmChainId.name} NFTs Accounts"))
            if (nftsFlow.isSuccess()) {
                val results = nftsFlow.data
                results.result
                    ?.filter { it.possibleSpam != true }
                    ?.forEach { nft ->
                        if (isValidNft(nft)) {
                            processedTokens.add(
                                EvmProcessorHelper.createEvmNftAccountItem(
                                    nftResult = nft,
                                    evmChainId = evmChainId.chain
                                )
                            )
                        }
                    }
            }
            else if (nftsFlow.isFailure()) {
                emit(EvmChainUpdate.Error("${evmChainId.name} NFTs ${nftsFlow.errorMessage}"))
            }

            // 5. Calculate wallet balances
            emit(EvmChainUpdate.LoadingStage("// Calculating ${evmChainId.name} wallet balances"))
            val totalBalanceUsd = processedTokens.sumOf { token ->
                token.valueUsd ?: 0.0
            }
            val totalBalanceNative =
                if (ethPriceUsd > 0) totalBalanceUsd / ethPriceUsd else 0.0

            emit(EvmChainUpdate.Success(totalBalanceUsd, totalBalanceNative, processedTokens))
        }
            .catch {
                emit(EvmChainUpdate.Error(it.message ?: it.cause?.message ?: "Unknown error"))
            }
            .collect { update -> emit(update) }
    }
}

/** this refers to the individual EvmChains */
sealed class EvmChainUpdate {
    data class LoadingStage(val stage: String) : EvmChainUpdate()
    data class Error(val message: String) : EvmChainUpdate()
    data class Success(
        val totalUsd: Double,
        val totalNative: Double,
        val tokens: List<TokenAccount>
    ) : EvmChainUpdate()
}