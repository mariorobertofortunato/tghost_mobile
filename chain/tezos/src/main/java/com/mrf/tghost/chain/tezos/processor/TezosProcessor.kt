package com.mrf.tghost.chain.tezos.processor

import com.mrf.tghost.domain.model.Result
import com.mrf.tghost.domain.model.SupportedChain
import com.mrf.tghost.domain.model.TokenAccount
import com.mrf.tghost.domain.model.TokenMarketDataInfo
import com.mrf.tghost.domain.model.WalletUpdate
import com.mrf.tghost.domain.model.isSuccess
import com.mrf.tghost.domain.model.isFailure
import com.mrf.tghost.domain.usecase.GetMarketDataUseCase
import com.mrf.tghost.chain.tezos.domain.usecase.GetStakingAccountsUseCase
import com.mrf.tghost.chain.tezos.domain.usecase.GetTokenAccountsUseCase
import com.mrf.tghost.chain.tezos.domain.usecase.GetTxUseCase
import com.mrf.tghost.chain.tezos.domain.usecase.GetWalletBalanceUseCase
import com.mrf.tghost.chain.tezos.utils.TEZOS_DECIMALS
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

class TezosProcessor @Inject constructor (
    private val walletBalanceUseCase: GetWalletBalanceUseCase,
    private val tokenAccountsUseCase: GetTokenAccountsUseCase,
    private val stakingAccountsUseCase: GetStakingAccountsUseCase,
    private val getMarketDataUseCase: GetMarketDataUseCase,
    private val getTxUseCase: GetTxUseCase
){

    fun processTezosWallet(publicKey: String) = flow {

        suspend fun getMarketData(): Deferred<Result<TokenMarketDataInfo?>> {
            return coroutineScope {
                async {
                    getMarketDataUseCase.fetchMarketDataInfo(
                        marketDataUrl = null,
                        address = null,
                        chain = SupportedChain.TEZ
                    ).last()
                }
            }
        }

        emit(WalletUpdate.LoadingStage("// Loading Tezos Wallet"))

        val balanceFlow = walletBalanceUseCase.balanceTezos(publicKey).distinctUntilChanged()
        val tokenAccountsFlow = tokenAccountsUseCase.tezosTokenAccounts(publicKey).distinctUntilChanged()
        val stakingAccountsFlow = stakingAccountsUseCase.tezosStakingAccounts(publicKey).distinctUntilChanged()
        val txFlow = getTxUseCase.txTezos(publicKey).distinctUntilChanged()

        combineTransform(
            balanceFlow,
            tokenAccountsFlow,
            stakingAccountsFlow,
            txFlow
        ) { balanceFlow, tokenAccountsFlow, stakingAccountsFlow, txFlow ->

            if (balanceFlow == null || tokenAccountsFlow == null || stakingAccountsFlow == null || txFlow == null) {
                emit(WalletUpdate.LoadingStage("// Synchronizing blockchain data…"))
                return@combineTransform
            }

            val processedTokens = mutableListOf<TokenAccount>()
            var xtzPriceUsd = 0.0

            // 1. XTZ Native token
            if (balanceFlow.isSuccess()) {
                emit(WalletUpdate.LoadingStage("// Processing native XTZ"))
                val nativeAmount = balanceFlow.data
                val tezosQuote = getMarketData().await()
                if (tezosQuote.isSuccess()) {
                    val tezosTokenAccount =
                        TezosProcessorHelper.createTezosNativeTokenAccountItem(
                            tezosPriceUSd = tezosQuote.data?.priceUsd?.toDouble() ?: 0.0,
                            tezosAmount = nativeAmount.toDouble()
                        )
                    processedTokens.add(tezosTokenAccount)

                    xtzPriceUsd = tezosQuote.data?.priceUsd?.toDouble() ?: 0.0

                    emit(WalletUpdate.Success(tokens = processedTokens))
                }
            }
            else if (balanceFlow.isFailure()) {
                emit(WalletUpdate.Error("Balance: ${balanceFlow.errorMessage}"))
            }

            // 2. Tezos Baking accounts
            if (stakingAccountsFlow.isSuccess()) {
                emit(WalletUpdate.LoadingStage("// Processing Tezos baking accounts"))
                val stakes = stakingAccountsFlow.data
                stakes.forEach { stake ->
                    val stakedTezAmount = stake.amount.divide(TEZOS_DECIMALS, 6, RoundingMode.HALF_UP)
                    if (stakedTezAmount > BigDecimal.ZERO) {
                        processedTokens.add(
                            TezosProcessorHelper.createStakedTezAccountItem(
                                bakerAlias = stake.bakerAlias,
                                bakerAddress = stake.bakerAddress,
                                stakedTezAmount = stakedTezAmount.toDouble(),
                                tezPriceUsd = xtzPriceUsd
                            )
                        )
                        emit(WalletUpdate.Success(tokens = processedTokens))
                    }
                }
            }
            else if (stakingAccountsFlow.isFailure()) {
                emit(WalletUpdate.Error("Baking Accounts: ${stakingAccountsFlow.errorMessage}"))
            }

            // 3. FA1.2 / FA2 token balances (fungibles + NFTs; categorizazion parsed from metadata)
            if (tokenAccountsFlow.isSuccess()) {
                emit(WalletUpdate.LoadingStage("// Processing Tezos tokens"))
                val tokenList = tokenAccountsFlow.data
                tokenList
                    .filter {
                        it.balance.toDouble() > 0.0
                    }
                    .forEach { tokenAccount ->
                        val tokenItem =
                            TezosProcessorHelper.createTezosTokenAccountItem(
                                tokenAccountDetails = tokenAccount
                            )
                        processedTokens.add(tokenItem)
                    }
                emit(WalletUpdate.Success(tokens = processedTokens))
            }
            else if (tokenAccountsFlow.isFailure()) {
                emit(WalletUpdate.Error("Tezos tokens: ${tokenAccountsFlow.errorMessage}"))
            }

            // 4. Tezos transactions
            if (txFlow.isSuccess()) {
                emit(WalletUpdate.LoadingStage("// Processing transactions"))
                emit(WalletUpdate.Success(transactions = txFlow.data))
            } else if (txFlow.isFailure()) {
                emit(WalletUpdate.Error("Transactions: ${txFlow.errorMessage}"))
            }

            emit(WalletUpdate.LoadingStage(null))

        }
            .catch {
                emit(WalletUpdate.Error(it.message ?: it.cause?.message ?: "Unknown error"))
            }
            .collect { update -> emit(update) }
    }

}
