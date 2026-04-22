package com.mrf.tghost.chain.solana.processor

import com.mrf.tghost.chain.solana.domain.usecase.GetNftUseCase
import com.mrf.tghost.chain.solana.domain.usecase.GetOnChainMetadataUseCase
import com.mrf.tghost.chain.solana.domain.usecase.GetStakingAccountsUseCase
import com.mrf.tghost.chain.solana.domain.usecase.GetTokenAccountsUseCase
import com.mrf.tghost.chain.solana.domain.usecase.GetTxUseCase
import com.mrf.tghost.chain.solana.domain.usecase.GetWalletBalanceUseCase
import com.mrf.tghost.chain.solana.processor.SolanaProcessorHelper.isValidNft
import com.mrf.tghost.chain.solana.utils.LAMPORTS_IN_SOL
import com.mrf.tghost.chain.solana.utils.SOLANA_TOKEN_MINT
import com.mrf.tghost.data.utils.MARKET_DATA_URL_DEXSCREENER
import com.mrf.tghost.domain.model.Result
import com.mrf.tghost.domain.model.SupportedChain
import com.mrf.tghost.domain.model.TokenAccount
import com.mrf.tghost.domain.model.TokenMarketDataInfo
import com.mrf.tghost.domain.model.Transaction
import com.mrf.tghost.domain.model.WalletUpdate
import com.mrf.tghost.domain.model.isFailure
import com.mrf.tghost.domain.model.isSuccess
import com.mrf.tghost.domain.model.metadata.MetadataHelpers.combineMetadata
import com.mrf.tghost.domain.usecase.GetMarketDataUseCase
import com.mrf.tghost.domain.usecase.GetOffChainMetadataUseCase
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.lastOrNull
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

class SolanaProcessor @Inject constructor(
    private val walletBalanceUseCase: GetWalletBalanceUseCase,
    private val tokenAccountsUseCase: GetTokenAccountsUseCase,
    private val stakingAccountsUseCase: GetStakingAccountsUseCase,
    private val nftUseCase: GetNftUseCase,
    private val onChainMetadataUseCase: GetOnChainMetadataUseCase,
    private val offChainMetadataUseCase: GetOffChainMetadataUseCase,
    private val getMarketDataUseCase: GetMarketDataUseCase,
    private val getTxUseCase: GetTxUseCase
) {

    fun processSolanaWallet(publicKey: String): Flow<WalletUpdate> = flow {

        suspend fun getMarketData(address: String): Deferred<Result<TokenMarketDataInfo?>> {
            return coroutineScope {
                async {
                    getMarketDataUseCase.fetchMarketDataInfo(
                        MARKET_DATA_URL_DEXSCREENER,
                        address,
                        SupportedChain.SOLANA
                    ).last()
                }
            }
        }

        val balanceFlow = walletBalanceUseCase.balanceSolana(publicKey).distinctUntilChanged()
        val tokenAccountsFlow = tokenAccountsUseCase.solanaTokenAccounts(publicKey).distinctUntilChanged()
        val stakingAccountsFlow = stakingAccountsUseCase.solanaStakingAccounts(publicKey).distinctUntilChanged()
        val nftFlow = nftUseCase.solanaNftAccounts(publicKey).distinctUntilChanged()
        val txFlow = getTxUseCase.txSolana(publicKey).distinctUntilChanged()

        combineTransform(
            balanceFlow,
            tokenAccountsFlow,
            stakingAccountsFlow,
            nftFlow,
            txFlow
        ) { balanceFlow, tokenAccountsFlow, stakingAccountsFlow, nftsFlow, txFlow ->

            if (balanceFlow == null || tokenAccountsFlow == null || stakingAccountsFlow == null || nftsFlow == null || txFlow == null) {
                emit(WalletUpdate.LoadingStage("// Synchronizing blockchain data…"))
                return@combineTransform
            }

            val processedTokens = mutableListOf<TokenAccount>()
            var solPriceUsdString = ""
            var solPriceUsd = 0.0

            // 1. SOL Native token
            if (balanceFlow.isSuccess()) {
                emit(WalletUpdate.LoadingStage("// Processing native SOL"))
                val nativeLamports = balanceFlow.data
                val nativeAmount =
                    BigDecimal(nativeLamports).divide(LAMPORTS_IN_SOL, 9, RoundingMode.HALF_UP)
                val solanaQuote = getMarketData(SOLANA_TOKEN_MINT).await()
                if (solanaQuote.isSuccess()) {
                    val solanaTokenAccount =
                        SolanaProcessorHelper.createSolanaNativeTokenAccountItem(
                            solanaQuote = solanaQuote.data
                                ?: TokenMarketDataInfo(),
                            solBalance = nativeAmount.toDouble()
                        )
                    processedTokens.add(solanaTokenAccount)

                    solPriceUsdString = solanaQuote.data?.priceUsd ?: ""
                    solPriceUsd = solPriceUsdString.toDoubleOrNull() ?: 0.0

                    emit(WalletUpdate.Success(tokens = processedTokens))
                }
            } else if (balanceFlow.isFailure()) {
                emit(WalletUpdate.Error("Balance: ${balanceFlow.errorMessage}"))
            }

            // 2. Solana SPL Tokens
            if (tokenAccountsFlow.isSuccess()) {
                emit(WalletUpdate.LoadingStage("// Processing Solana SPL Tokens"))
                val tokenList = tokenAccountsFlow.data
                tokenList
                    .filter {
                        (it.account.data.parsed.info.tokenAmount.uiAmount ?: 0.0) > 0.0 ||
                                (it.account.lamports > 0L)
                    }
                    .forEach { tokenAccount ->

                        val mint = tokenAccount.account.data.parsed.info.mint
                        val metadataPDA =
                            SolanaProcessorHelper.createMetadataPDAString(mint)

                        val onChainMetadata = try {
                            val res =
                                onChainMetadataUseCase.getSolanaTokenOnChainMetadata(
                                    metadataPDA
                                )
                                    .lastOrNull()
                            if (res is Result.Success) res.data else null
                        } catch (e: Exception) {
                            null
                        }

                        val offChainMetadata = try {
                            val res =
                                offChainMetadataUseCase.getOffChainMetadata(
                                    onChainMetadata?.uri ?: ""
                                )
                                    .lastOrNull()
                            if (res is Result.Success) res.data else null
                        } catch (e: Exception) {
                            null
                        }

                        val marketData = getMarketData(mint).await()

                        val marketDataInfo = if (marketData.isSuccess()) {
                            marketData.data
                        } else null

                        val combinedMetadata =
                            combineMetadata(onChainMetadata, offChainMetadata)

                        val tokenItem =
                            SolanaProcessorHelper.createSolanaTokenAccountItem(
                                tokenAccountDetails = tokenAccount,
                                metadata = combinedMetadata,
                                marketDataInfo = marketDataInfo,
                                priceUsdSolana = solPriceUsdString
                            )
                        processedTokens.add(tokenItem)
                    }

                emit(WalletUpdate.Success(tokens = processedTokens))

            } else if (tokenAccountsFlow.isFailure()) {
                emit(WalletUpdate.Error("SPL tokens: ${tokenAccountsFlow.errorMessage}"))
            }

            // 3. Staking Accounts
            if (stakingAccountsFlow.isSuccess()) {
                emit(WalletUpdate.LoadingStage("// Processing Staking Accounts"))
                val stakes = stakingAccountsFlow.data
                val totalStakedLamports = stakes.sumOf { it.amount }
                val stakedSolAmount =
                    totalStakedLamports.divide(LAMPORTS_IN_SOL, 9, RoundingMode.HALF_UP)
                if (stakedSolAmount > BigDecimal.ZERO) {
                    processedTokens.add(
                        SolanaProcessorHelper.createStakedSolAccountItem(
                            stakedSolAmount = stakedSolAmount,
                            solPriceUsd = solPriceUsd
                        )
                    )
                    emit(WalletUpdate.Success(tokens = processedTokens))
                }
            } else if (stakingAccountsFlow.isFailure()) {
                emit(WalletUpdate.Error("Staking Accounts: ${stakingAccountsFlow.errorMessage}"))
            }

            // 4. Solana NFTs
            if (nftsFlow.isSuccess()) {
                emit(WalletUpdate.LoadingStage("// Processing Solana NFTs"))
                val results = nftsFlow.data
                results.result?.items?.forEach { nft ->
                    if (isValidNft(nft)) {
                        processedTokens.add(
                            SolanaProcessorHelper.createSolanaNftAccountItem(
                                dasAsset = nft
                            )
                        )
                    }
                }
                emit(WalletUpdate.Success(tokens = processedTokens))
            } else if (nftsFlow.isFailure()) {
                emit(WalletUpdate.Error("NFTs: ${nftsFlow.errorMessage}"))
            }

            // 5. Solana transactions
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
