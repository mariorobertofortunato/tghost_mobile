package com.mrf.tghost.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrf.tghost.app.contracts.WalletDetailsScreenEvent
import com.mrf.tghost.app.contracts.WalletDetailsScreenState
import com.mrf.tghost.app.contracts.WalletState
import com.mrf.tghost.app.ui.composables.ViewState
import com.mrf.tghost.chain.evm.processor.EvmProcessor
import com.mrf.tghost.chain.solana.processor.SolanaProcessor
import com.mrf.tghost.chain.sui.processor.SuiProcessor
import com.mrf.tghost.chain.tezos.processor.TezosProcessor
import com.mrf.tghost.domain.model.SupportedChainId
import com.mrf.tghost.domain.model.TokenAccount
import com.mrf.tghost.domain.model.TokenAccountCategories
import com.mrf.tghost.domain.model.Wallet
import com.mrf.tghost.domain.model.WalletSnapshot
import com.mrf.tghost.domain.model.WalletUpdate
import com.mrf.tghost.domain.usecase.localwallets.DeleteWalletUseCase
import com.mrf.tghost.domain.usecase.localwallets.GetWalletsUseCase
import com.mrf.tghost.domain.usecase.localwallets.UpdateWalletUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalletDetailsViewModel @Inject constructor(
    private val getWalletsUseCase: GetWalletsUseCase,
    private val updateWalletUseCase: UpdateWalletUseCase,
    private val deleteWalletUseCase: DeleteWalletUseCase,
    private val solanaProcessor: SolanaProcessor,
    private val evmProcessor: EvmProcessor,
    private val suiProcessor: SuiProcessor,
    private val tezosProcessor: TezosProcessor
) : ViewModel() {

    private var fetchJob: Job? = null

    private val _state = MutableStateFlow(
        WalletDetailsScreenState(
            eventTunnel = { event ->
                processEvent(event)
            }
        )
    )
    val state: StateFlow<WalletDetailsScreenState> = _state.asStateFlow()

    private fun processEvent(event: WalletDetailsScreenEvent) {
        when (event) {
            is WalletDetailsScreenEvent.DisconnectWallet -> disconnectWallet(
                event.publicKey,
                event.onDisconnectSuccess
            )

            is WalletDetailsScreenEvent.UpdateWallet -> updateDbWallet(
                event.wallet,
                event.refreshUi
            )

            is WalletDetailsScreenEvent.RefreshWallet -> fetchWallet(event.publicKey)
            is WalletDetailsScreenEvent.FetchWallet -> fetchWallet(event.publicKey)
            is WalletDetailsScreenEvent.OpenPopup -> _state.update { it.copy(popupType = event.popupType) }
            is WalletDetailsScreenEvent.ClosePopup -> _state.update { it.copy(popupType = null) }
        }
    }

    private fun fetchWallet(publicKey: String?) {

        if (publicKey.isNullOrBlank()) return

        fetchJob?.cancel()
        fetchJob = viewModelScope.launch(Dispatchers.IO) {

            val wallet = getWalletsUseCase.getWallet(publicKey)

            val walletState = WalletState(
                wallet = Wallet(
                    publicKey = wallet?.publicKey ?: "",
                    name = wallet?.name ?: "",
                    chainId = wallet?.chainId ?: SupportedChainId.SOL,
                    snapshot = wallet?.snapshot
                ),
                walletViewState = ViewState.Loading
            )
            _state.update { it.copy(wallet = walletState) }
            observeWalletData(_state.value.wallet?.wallet)
        }


    }

    private fun disconnectWallet(publicKey: String, onDisconnectSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteWalletUseCase(publicKey)
        }.invokeOnCompletion {
            viewModelScope.launch(Dispatchers.Main) {
                onDisconnectSuccess()
            }
        }
    }

    private fun updateDbWallet(wallet: Wallet, refreshUi: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            updateWalletUseCase(wallet)
        }.invokeOnCompletion {
            if (refreshUi) {
                fetchWallet(wallet.publicKey)
            }
        }
    }

    private suspend fun observeWalletData(wallet: Wallet?) {

        val publicKey = wallet?.publicKey ?: return

        when (wallet.chainId) {

            SupportedChainId.SOL -> {
                    solanaProcessor.processSolanaWallet(publicKey).collect { update ->
                        handleWalletUpdate(publicKey, wallet, update)
                    }
            }

            SupportedChainId.EVM -> {
                    evmProcessor.processEvmWallet(publicKey).collect { update ->
                        handleWalletUpdate(publicKey, wallet, update)
                    }
            }

            SupportedChainId.SUI -> {
                    suiProcessor.processSuiWallet(publicKey).collect { update ->
                        handleWalletUpdate(publicKey, wallet, update)
                    }
            }

            SupportedChainId.TEZ -> {
                    tezosProcessor.processTezosWallet(publicKey).collect { update ->
                        handleWalletUpdate(publicKey, wallet, update)
                    }
            }

        }


    }

    private fun handleWalletUpdate(publicKey: String, wallet: Wallet, update: WalletUpdate) {
        when (update) {
            is WalletUpdate.LoadingStage -> {
                updateWalletState {
                    it.copy(loadingStage = update.stage)
                }
            }

            is WalletUpdate.Success -> {
                var snapshotBalanceUsd = 0.0
                var snapshotBalanceNative = 0.0
                updateWalletState { currentState ->

                    // This is needed when Live Update is enabled,
                    // in order not to empty the list while waiting
                    // for updated tokens to finish to be processed
                    val map = LinkedHashMap<String, TokenAccount>()
                    currentState.tokenAccounts.forEach { map[it.pubkey ?: ""] = it }
                    update.tokens.forEach { map[it.pubkey ?: ""] = it }
                    if (currentState.loadingStage == null) {
                        val newKeys = update.tokens.map { it.pubkey }.toSet()
                        map.keys.retainAll { it in newKeys }
                    }
                    val updatedTokens = map.values.toList().sortedBy { it.amountDouble ?: 0.0 }

                    snapshotBalanceUsd = updatedTokens.sumOf { it.valueUsd ?: 0.0 }
                    snapshotBalanceNative = updatedTokens
                        .filter { it.tokenAccountCategory != TokenAccountCategories.NFTS }
                        .sumOf { it.valueEthEquivalent ?: it.valueNative ?: 0.0 }

                    currentState.copy(
                        walletViewState = ViewState.Success,
                        tokenAccounts = updatedTokens,
                        balanceUSd = snapshotBalanceUsd,
                        balanceNative = snapshotBalanceNative,
                        transactions = update.transactions.takeIf { it.isNotEmpty() } ?: currentState.transactions
                    )
                }
                updateDbWallet(
                    wallet = wallet.copy(
                        snapshot = WalletSnapshot(
                            timestamp = System.currentTimeMillis(),
                            balanceUSd = snapshotBalanceUsd,
                            balanceNative = snapshotBalanceNative
                        )
                    )
                )
            }

            is WalletUpdate.Error -> {
                updateWalletState {
                    it.copy(
                        walletViewState = ViewState.Error(message = update.message)
                    )
                }
            }
        }
    }

    private fun updateWalletState(
        update: (WalletState) -> WalletState
    ) {
        _state.update { currentState ->
            val updatedWallets = update(currentState.wallet ?: return@update currentState)
            currentState.copy(wallet = updatedWallets)
        }
    }
}
