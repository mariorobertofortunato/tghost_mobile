package com.mrf.tghost.chain.evm.processor

import com.mrf.tghost.chain.evm.domain.usecase.GetNftUseCase
import com.mrf.tghost.chain.evm.domain.usecase.GetStakingAccountsUseCase
import com.mrf.tghost.chain.evm.domain.usecase.GetTokenAccountsUseCase
import com.mrf.tghost.chain.evm.domain.usecase.GetTxUseCase
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
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject
import com.mrf.tghost.domain.model.Result
import com.mrf.tghost.domain.usecase.GetMarketDataUseCase
import com.mrf.tghost.chain.evm.processor.EvmProcessorHelper.nativeMintFor
import com.mrf.tghost.domain.model.RpcPreference
import com.mrf.tghost.domain.model.RpcProviderId
import com.mrf.tghost.domain.model.SupportedChainId
import com.mrf.tghost.domain.model.Transaction
import com.mrf.tghost.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.collections.forEach

class EvmProcessor @Inject constructor(
    private val walletBalanceUseCase: GetWalletBalanceUseCase,
    private val tokenAccountsUseCase: GetTokenAccountsUseCase,
    private val stakingAccountsUseCase: GetStakingAccountsUseCase,
    private val nftUseCase: GetNftUseCase,
    private val getMarketDataUseCase: GetMarketDataUseCase,
    private val getTxUseCase: GetTxUseCase,
    private val preferencesRepository: PreferencesRepository,
) {

    fun processEvmWallet(publicKey: String): Flow<WalletUpdate> = channelFlow {

        val provider: RpcPreference =
            preferencesRepository.getRpcPreference(SupportedChainId.EVM).first()
        val chainTokens = mutableMapOf<EvmChain, List<TokenAccount>>()
        val chainTx = mutableMapOf<EvmChain, List<Transaction>>()
        val mutex = Mutex()

        when (provider.providerId) {
            RpcProviderId.ALCHEMY -> {
                // per tutte le chain: una sola chiamata API per fetchare token accounts (compresi native) + una sola chiamata per nft + una sola chiamata per trnasfers
                // No defi positions
                // Le tx sono fetchate per-chain internamente al repository (perchè alchemy è un cazzo di brodo)
                var latestTokens: List<TokenAccount> = emptyList()
                var latestTransactions: List<Transaction> = emptyList()

                processEvmChain(evmChainId = null, publicKey = publicKey).collect { update ->
                    when (update) {
                        is EvmChainUpdate.LoadingStage -> {
                            send(WalletUpdate.LoadingStage(update.stage))
                        }

                        is EvmChainUpdate.Success -> {
                            if (update.tokens.isNotEmpty() || latestTokens.isEmpty()) {
                                latestTokens = update.tokens
                            }
                            if (update.transactions.isNotEmpty() || latestTransactions.isEmpty()) {
                                latestTransactions = update.transactions
                            }
                            send(
                                WalletUpdate.Success(
                                    tokens = latestTokens,
                                    transactions = latestTransactions,
                                )
                            )
                        }

                        is EvmChainUpdate.Error -> {
                            send(WalletUpdate.Error(update.message))
                        }
                    }
                }
            }

            RpcProviderId.MORALIS -> {
                // una chiamata PER OGNI CHAIN per fetchare token accounts (compresi native) +
                // una chiamata PER OGNI CHAIN per fetchare nft +
                // una chiamata PER OGNI CHAIN per fetchare defi positions +
                // una chiamata PER OGNI CHAIN per fetchare transfers

                EvmChain.entries.forEach { chain ->
                    launch {
                        processEvmChain(
                            evmChainId = chain,
                            publicKey = publicKey
                        ).collect { update ->
                            when (update) {
                                is EvmChainUpdate.LoadingStage -> {
                                    send(WalletUpdate.LoadingStage(update.stage))
                                }

                                is EvmChainUpdate.Success -> {
                                    val mergedTokens = mutex.withLock {
                                        if (update.tokens.isNotEmpty() || !chainTokens.containsKey(chain)) {
                                            chainTokens[chain] = update.tokens
                                        }
                                        chainTokens.values.flatten()
                                    }
                                    val mergedTx = mutex.withLock {
                                        if (update.transactions.isNotEmpty() || !chainTx.containsKey(chain)) {
                                            chainTx[chain] = update.transactions
                                        }
                                        chainTx.values.flatten()
                                    }
                                    send(WalletUpdate.Success(tokens = mergedTokens, transactions = mergedTx))
                                    send(WalletUpdate.LoadingStage(null))
                                }

                                is EvmChainUpdate.Error -> {
                                    send(WalletUpdate.Error(update.message))
                                }
                            }
                        }

                    }

                }
            }

            else -> {
                // una chiamata PER OGNI CHAIN rpc eth_getBalance
                // No erc-20, no nft, no defi, no activity
                EvmChain.entries.forEach { chainId ->
                    launch {
                        suspend fun getMarketData(address: String): Deferred<Result<TokenMarketDataInfo?>> {
                            return coroutineScope {
                                async {
                                    getMarketDataUseCase.fetchMarketDataInfo(
                                        MARKET_DATA_URL_DEXSCREENER,
                                        address,
                                        SupportedChain.EVM,
                                        if (chainId.chain == "eth") "ethereum" else chainId.chain
                                    ).last()
                                }
                            }
                        }

                        val balanceFlow = walletBalanceUseCase.balanceEvm(publicKey, chainId)
                            .distinctUntilChanged()

                        balanceFlow.collect { update ->
                            if (update == null) {
                                send(WalletUpdate.LoadingStage("// Synchronizing ${chainId.name} data…"))
                                return@collect
                            }

                            if (update.isSuccess()) {
                                send(WalletUpdate.LoadingStage("// Processing native ${chainId.name} coins"))
                                val nativeAmount = update.data
                                val nativeQuote =
                                    getMarketData(nativeMintFor(chainId.chain)).await()
                                if (nativeQuote.isSuccess()) {
                                    val ethereumTokenAccount =
                                        EvmProcessorHelper.createEthNativeTokenAccountItem(
                                            address = nativeMintFor(chainId.chain),
                                            quote = nativeQuote.data ?: TokenMarketDataInfo(),
                                            balance = nativeAmount
                                        )

                                    val mergedTokens = mutex.withLock {
                                        chainTokens[chainId] = listOf(ethereumTokenAccount)
                                        chainTokens.values.flatten()
                                    }
                                    send(WalletUpdate.Success(tokens = mergedTokens))
                                    send(WalletUpdate.LoadingStage(null))
                                }
                            } else if (update.isFailure()) {
                                send(WalletUpdate.Error("${chainId.name} balance: ${update.errorMessage}"))
                            }
                        }

                    }
                }
            }
        }

    }.catch {
        emit(WalletUpdate.Error(it.message ?: it.cause?.message ?: "Unknown error"))
    }

    /**
     * Processes a single EVM chain
     */
    private fun processEvmChain(evmChainId: EvmChain?, publicKey: String): Flow<EvmChainUpdate> =
        flow {

            suspend fun getMarketData(
                address: String,
                dexChainId: String
            ): Deferred<Result<TokenMarketDataInfo?>> {
                return coroutineScope {
                    async {
                        getMarketDataUseCase.fetchMarketDataInfo(
                            MARKET_DATA_URL_DEXSCREENER,
                            address,
                            SupportedChain.EVM,
                            dexChainId
                        ).last()
                    }
                }
            }

            val tokenAccountsFlow = tokenAccountsUseCase.evmTokenAccounts(publicKey, evmChainId).distinctUntilChanged()
            val stakingAccountsFlow = stakingAccountsUseCase.evmStakingAccounts(publicKey, evmChainId).distinctUntilChanged()
            val nftFlow = nftUseCase.evmNFTSAccounts(publicKey, evmChainId).distinctUntilChanged()
            val activityFlow = getTxUseCase.txEvm(publicKey, evmChainId).distinctUntilChanged()


            combineTransform(
                tokenAccountsFlow,
                stakingAccountsFlow,
                nftFlow,
                activityFlow
            ) { tokenAccountsFlow, stakingAccountsFlow, nftsFlow, activityFlow ->
                if (tokenAccountsFlow == null || stakingAccountsFlow == null || nftsFlow == null || activityFlow == null) {
                    emit(EvmChainUpdate.LoadingStage("// Synchronizing ${evmChainId?.name} data…"))
                    return@combineTransform
                }

                val processedTokens = mutableListOf<TokenAccount>()

                val tokenListSnapshot =
                    if (tokenAccountsFlow.isSuccess()) {
                        tokenAccountsFlow.data.filter { it.balance > BigDecimal.ZERO }
                    } else {
                        emptyList()
                    }

                val dexChainsForEth = buildSet {
                    tokenListSnapshot.forEach {
                        add(EvmProcessorHelper.resolveDexChainId(it.network, evmChainId?.chain))
                    }
                    add(EvmProcessorHelper.resolveDexChainId(null, evmChainId?.chain))
                }

                val ethUsdByDexChain: Map<String, Double> = coroutineScope {
                    dexChainsForEth.map { dcid ->
                        async(Dispatchers.IO) {
                            val chainKey = when (dcid) {
                                "ethereum" -> "eth"
                                "base" -> "base"
                                else -> "eth"
                            }
                            val mint = nativeMintFor(chainKey)
                            val r = getMarketData(mint, dcid).await()
                            dcid to if (r.isSuccess()) r.data?.priceUsdDouble ?: 0.0 else 0.0
                        }
                    }.awaitAll().toMap()
                }

                val stakeEthUsd =
                    ethUsdByDexChain[EvmProcessorHelper.resolveDexChainId(null, evmChainId?.chain)]
                        ?: 0.0

                // 1. ERC-20 Token Accounts + Native tokens
                if (tokenAccountsFlow.isSuccess()) {
                    emit(EvmChainUpdate.LoadingStage("// Processing ${evmChainId?.name} ERC-20 Token Accounts"))
                    val tokenDetails = coroutineScope {
                        tokenListSnapshot.map { tokenAccount ->
                            async(Dispatchers.IO) {
                                val dexChainId =
                                    EvmProcessorHelper.resolveDexChainId(tokenAccount.network, evmChainId?.chain)
                                val marketData =
                                    getMarketData(
                                        tokenAccount.contractAddress,
                                        dexChainId,
                                    ).await()

                                val marketDataInfo = if (marketData.isSuccess()) {
                                    marketData.data
                                } else {
                                    null
                                }

                                Triple(tokenAccount, marketDataInfo, dexChainId)
                            }
                        }.awaitAll()
                    }
                    tokenDetails
                        .filter { (tokenAccount, marketDataInfo, _) ->
                            val marketUsd = marketDataInfo?.priceUsdDouble ?: 0.0
                            val alchemyUsd = tokenAccount.prices
                                .firstOrNull {
                                    it.currency?.equals(
                                        "usd",
                                        ignoreCase = true
                                    ) == true
                                }
                                ?.value
                                ?.toDoubleOrNull() ?: 0.0
                            marketUsd > 0.00001 ||
                                alchemyUsd > 0.00001 ||
                                (tokenAccount.isNative && tokenAccount.balance > BigDecimal.ZERO)
                        }
                        .forEach { (tokenAccount, marketDataInfo, dexChainId) ->
                            val ethUsd = ethUsdByDexChain[dexChainId] ?: 0.0
                            val tokenItem =
                                EvmProcessorHelper.createEvmTokenAccountItem(
                                    tokenAccountDetails = tokenAccount,
                                    marketDataInfo = marketDataInfo,
                                    evmChainId = evmChainId?.chain,
                                    ethUsdPrice = ethUsd,
                                )
                            processedTokens.add(tokenItem)
                        }
                    emit(EvmChainUpdate.Success(tokens = processedTokens))
                } else if (tokenAccountsFlow.isFailure()) {
                    emit(EvmChainUpdate.Error("${evmChainId?.name} tokens: ${tokenAccountsFlow.errorMessage}"))
                }

                // 2. Staking Accounts
                if (stakingAccountsFlow.isSuccess()) {
                    emit(EvmChainUpdate.LoadingStage("// Processing ${evmChainId?.name} Native Staking Accounts"))
                    val stakes = stakingAccountsFlow.data
                    stakes.forEach { stakeAccount ->
                        val posBalanceUsd = stakeAccount.position?.balanceUsd ?: 0.0
                        if (posBalanceUsd > 0.0) {
                            processedTokens.add(
                                EvmProcessorHelper.createStakedEthAccountItem(
                                    stakeAccount = stakeAccount,
                                    evmChainId = evmChainId?.chain,
                                    ethUsdPrice = stakeEthUsd,
                                )
                            )
                            emit(EvmChainUpdate.Success(tokens = processedTokens))
                        }
                    }
                } else if (stakingAccountsFlow.isFailure()) {
                    emit(EvmChainUpdate.Error("${evmChainId?.name} Staking Accounts: ${stakingAccountsFlow.errorMessage}"))
                }

                // 3. EVM NFTs
                if (nftsFlow.isSuccess()) {
                    emit(EvmChainUpdate.LoadingStage("// Processing ${evmChainId?.name} NFTs Accounts"))
                    val results = nftsFlow.data
                    results.result
                        ?.filter { it.possibleSpam != true }
                        ?.forEach { nft ->
                            if (isValidNft(nft)) {
                                processedTokens.add(
                                    EvmProcessorHelper.createEvmNftAccountItem(
                                        nftResult = nft,
                                        evmChainId = evmChainId?.chain
                                    )
                                )
                                emit(EvmChainUpdate.Success(tokens = processedTokens))
                            }
                        }
                } else if (nftsFlow.isFailure()) {
                    emit(EvmChainUpdate.Error("${evmChainId?.name} NFTs ${nftsFlow.errorMessage}"))
                }

                // 4. EVM transactions
                if (activityFlow.isSuccess()) {
                    emit(EvmChainUpdate.LoadingStage("// Processing transactions"))
                    emit(EvmChainUpdate.Success(transactions = activityFlow.data))
                } else if (activityFlow.isFailure()) {
                    emit(EvmChainUpdate.Error("Transactions: ${activityFlow.errorMessage}"))
                }

                emit(EvmChainUpdate.LoadingStage(null))
            }
                .catch {
                    emit(EvmChainUpdate.Error(it.message ?: it.cause?.message ?: "Unknown error"))
                }
                .collect { update -> emit(update) }
        }
}

/** this refers to the individual EvmChains */
sealed class EvmChainUpdate {
    data class LoadingStage(val stage: String?) : EvmChainUpdate()
    data class Error(val message: String) : EvmChainUpdate()
    data class Success(
        val tokens: List<TokenAccount> = emptyList(),
        val transactions: List<Transaction> = emptyList()
    ) : EvmChainUpdate()
}