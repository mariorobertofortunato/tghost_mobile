package com.mrf.tghost.chain.sui.processor

import com.mrf.tghost.chain.sui.domain.usecase.GetOwnedObjectsUseCase
import com.mrf.tghost.chain.sui.domain.usecase.GetOnChainMetadataUseCase
import com.mrf.tghost.chain.sui.domain.usecase.GetStakingAccountsUseCase
import com.mrf.tghost.chain.sui.domain.usecase.GetWalletActivityUseCase
import com.mrf.tghost.domain.model.Result
import com.mrf.tghost.domain.model.SupportedChain
import com.mrf.tghost.domain.model.TokenAccount
import com.mrf.tghost.domain.model.TokenMarketDataInfo
import com.mrf.tghost.domain.model.WalletUpdate
import com.mrf.tghost.domain.model.isSuccess
import com.mrf.tghost.domain.model.isFailure
import com.mrf.tghost.domain.usecase.GetMarketDataUseCase
import com.mrf.tghost.domain.usecase.GetOffChainMetadataUseCase
import com.mrf.tghost.chain.sui.processor.SuiProcessorHelper.splitOwnedObjects
import com.mrf.tghost.data.utils.MARKET_DATA_URL_DEXSCREENER
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.lastOrNull
import javax.inject.Inject

class SuiProcessor @Inject constructor(
    private val stakingAccountsUseCase: GetStakingAccountsUseCase,
    private val ownedObjectsUseCase: GetOwnedObjectsUseCase,
    private val walletActivityUseCase: GetWalletActivityUseCase,
    private val onChainMetadataUseCase: GetOnChainMetadataUseCase,
    private val getMarketDataUseCase: GetMarketDataUseCase,
    private val offChainMetadataUseCase: GetOffChainMetadataUseCase
) {

    fun processSuiWallet(publicKey: String) = flow {

        suspend fun getMarketData(address: String): Deferred<Result<TokenMarketDataInfo?>> {
            return coroutineScope {
                async {
                    getMarketDataUseCase.fetchMarketDataInfo(
                        MARKET_DATA_URL_DEXSCREENER,
                        address,
                        SupportedChain.SUI
                    ).last()
                }
            }
        }

        emit(WalletUpdate.LoadingStage("// Loading Sui Wallet"))

        val stakingAccountsFlow = stakingAccountsUseCase.suiStakingAccounts(publicKey).distinctUntilChanged()
        val ownedObjectsFlow = ownedObjectsUseCase.suiOwnedObjects(publicKey).distinctUntilChanged()
        val walletActivityFlow = walletActivityUseCase.suiWalletActivity(publicKey).distinctUntilChanged()

        combineTransform(
            stakingAccountsFlow,
            ownedObjectsFlow,
            walletActivityFlow
        ) { _,
            ownedObjectsFlow,
            walletActivityFlow ->

            if (ownedObjectsFlow == null || walletActivityFlow == null) {
                emit(WalletUpdate.LoadingStage("// Synchronizing Sui data…"))
                return@combineTransform
            }

            val processedTokens = mutableListOf<TokenAccount>()
            var suiPriceUsd = 0.0

            // 1. Owned objects
            if (ownedObjectsFlow.isSuccess()) {
                emit(WalletUpdate.LoadingStage("// Processing Sui owned objects"))
                val split = splitOwnedObjects(ownedObjectsFlow.data.data)

                // Coins
                coroutineScope {
                    split.coinObjects
                        .filter { it.totalBalance.toDouble() > 0.0 }
                        .map { coin ->
                            async(Dispatchers.IO) {
                                val onChainMetadata = try {
                                    val res =
                                        onChainMetadataUseCase.getSuiCoinMetadata(coin.coinType)
                                            .lastOrNull()
                                    if (res is Result.Success) res.data else null
                                } catch (e: Exception) {
                                    null
                                }
                                val marketData = getMarketData(coin.coinType).await()
                                val marketDataInfo =
                                    if (marketData.isSuccess()) marketData.data else null
                                Triple(coin, onChainMetadata, marketDataInfo)
                            }
                        }
                        .awaitAll()
                        .filter { (it.third?.priceNativeDouble ?: 0.0) > 0.0 }
                        .forEach { (coin, metadata, marketDataInfo) ->
                            processedTokens.add(
                                SuiProcessorHelper.createSuiCoinItem(
                                    tokenAccountDetails = coin,
                                    metadata = metadata,
                                    marketDataInfo = marketDataInfo,
                                )
                            )
                        }
                }

                // NFTs
                coroutineScope {
                    val nftResults = mutableListOf<TokenAccount>()
                    split.nftObjects.map { nftAccount ->
                            async(Dispatchers.IO) {
                                val uri = SuiProcessorHelper.extractNftMetadataUri(nftAccount)
                                val offChainMetadata = try {
                                    val res = offChainMetadataUseCase.getOffChainMetadata(uri).lastOrNull()
                                    if (res is Result.Success) res.data else null
                                } catch (e: Exception) {
                                    null
                                }
                                SuiProcessorHelper.createSuiNftItem(
                                    nft = nftAccount,
                                    offChainMetadata = offChainMetadata
                                )
                            }
                        }.awaitAll().forEach { nftResults.add(it) }

                    nftResults.forEach { nftItem ->
                            processedTokens.add(nftItem)
                    }
                }

                suiPriceUsd = processedTokens.firstOrNull { it.pubkey == "0x2::sui::SUI" }
                    ?.priceUsd?.toDoubleOrNull() ?: 0.0

                // Native Staking
                split.stakedSuiObjects.forEach { staked ->
                    SuiProcessorHelper.createStakedSuiAccountItem(staked, suiPriceUsd)
                        ?.let { processedTokens.add(it) }
                }

                emit(WalletUpdate.Success(tokens = processedTokens))

            } else if (ownedObjectsFlow.isFailure()) {
                emit(WalletUpdate.Error("Sui wallet: ${ownedObjectsFlow.errorMessage}"))
            }

            if (walletActivityFlow.isSuccess()) {
                emit(WalletUpdate.LoadingStage("// Processing wallet activity"))
                emit(WalletUpdate.Success(transactions = walletActivityFlow.data))
            } else if (walletActivityFlow.isFailure()) {
                emit(WalletUpdate.Error("Wallet activity: ${walletActivityFlow.errorMessage}"))
            }

            // ... TODO Process Liquid Staking Accounts (cetus, suilend, ...)

            emit(WalletUpdate.LoadingStage(null))
        }
            .catch {
                emit(WalletUpdate.Error(it.message ?: it.cause?.message ?: "Unknown error"))
            }
            .collect { update -> emit(update) }
    }
}
