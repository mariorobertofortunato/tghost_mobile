package com.mrf.tghost.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrf.tghost.app.contracts.MainScreenEvent
import com.mrf.tghost.app.contracts.MainScreenState
import com.mrf.tghost.app.contracts.WalletState
import com.mrf.tghost.app.ui.composables.ViewState
import com.mrf.tghost.app.utils.enums.DrawerType
import com.mrf.tghost.app.utils.enums.PopupType
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
import com.mrf.tghost.domain.usecase.localwallets.AddWalletUseCase
import com.mrf.tghost.domain.usecase.localwallets.GetWalletsUseCase
import com.mrf.tghost.domain.usecase.localwallets.UpdateWalletUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val getWalletsUseCase: GetWalletsUseCase,
    private val addWalletUseCase: AddWalletUseCase,
    private val updateWalletUseCase: UpdateWalletUseCase,
    private val solanaProcessor: SolanaProcessor,
    private val evmProcessor: EvmProcessor,
    private val suiProcessor: SuiProcessor,
    private val tezosProcessor: TezosProcessor
) : ViewModel() {

    private var fetchJob: Job? = null

    private val _state = MutableStateFlow(
        MainScreenState(
            eventTunnel = { event ->
                processEvent(event)
            }
        )
    )
    val state: StateFlow<MainScreenState> = _state.asStateFlow()

    private fun processEvent(event: MainScreenEvent) {
        when (event) {
            is MainScreenEvent.FetchWallets -> fetchWallets()
            is MainScreenEvent.ConnectWallet -> connectWallet(event.publicKey, event.chain)
            is MainScreenEvent.OpenDrawer -> openDrawer(event.drawerType, event.data)
            is MainScreenEvent.CloseDrawer -> closeDrawer()
            is MainScreenEvent.OpenPopup -> openPopup(event.popupType)
            is MainScreenEvent.ClosePopup -> closePopup()
            is MainScreenEvent.OnDispose -> { fetchJob?.cancel() }
        }
    }

    /** POPUP */
    private fun openPopup(popup: PopupType) {
        _state.update { it.copy(popupType = popup) }
    }

    private fun closePopup() {
        _state.update { it.copy(popupType = null) }
    }

    /** DRAWER */
    private fun openDrawer(drawer: DrawerType, data: Any?) {
        if (data != null) {
            data as WalletState
            _state.update { it.copy(selectedWallet = WalletState(wallet = data.wallet)) }
        }
        _state.update { it.copy(drawerType = drawer) }
    }

    private fun closeDrawer() {
        _state.update { it.copy(selectedWallet = null, drawerType = null) }
    }

    private fun fetchWallets() {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            setScreenLoadingState()

            // We need only the first emission, this works also as a refresh
            val wallets = getWalletsUseCase.invoke().first()

            val walletStates = wallets.map { wallet ->
                WalletState(
                    wallet = Wallet(
                        publicKey = wallet.publicKey,
                        name = wallet.name,
                        chainId = wallet.chainId,
                        snapshot = wallet.snapshot
                    ),
                    walletViewState = ViewState.Loading
                )
            }

            _state.update { it.copy(wallets = walletStates) }

            if (_state.value.wallets.isEmpty()) {
                _state.update { it.copy(isLoading = false) }
                return@launch
            }

            walletStates.map { wallet ->
                async {
                    observeWalletData(wallet.wallet)
                }
            }.awaitAll()
        }


    }

    private fun connectWallet(publicKey: String, chain: SupportedChainId) {

        viewModelScope.launch(Dispatchers.IO) {
            setScreenLoadingState()
            if (_state.value.wallets.any { it.wallet?.publicKey == publicKey }) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        isError = "❌ | Wallet already exists."
                    )
                }
                return@launch
            }

            // Save to DB
            addWalletUseCase(
                Wallet(
                    publicKey = publicKey,
                    name = publicKey.take(4),
                    chainId = chain,
                    snapshot = null
                )
            )
            fetchWallets()
        }
    }

    private fun updateDbWallet(wallet: Wallet) {
        viewModelScope.launch(Dispatchers.IO) {
            updateWalletUseCase(wallet)
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
                updateWalletState(publicKey) {
                    it.copy(loadingStage = update.stage)
                }
            }

            is WalletUpdate.Success -> {
                var snapshotBalanceUsd = 0.0
                var snapshotBalanceNative = 0.0
                updateWalletState(publicKey) { currentState ->

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
                calculatePortfolioBalance()
            }
            is WalletUpdate.Error -> {
                updateWalletState(publicKey) {
                    it.copy(
                        errorList = (it.errorList + update.message).distinct()
                    )
                }
            }
        }
    }

    private fun updateWalletState(
        publicKey: String,
        update: (WalletState) -> WalletState
    ) {
        _state.update { currentState ->
            val updatedWallets = currentState.wallets.map { wallet ->
                if (wallet.wallet?.publicKey == publicKey) {
                    update(wallet)
                } else {
                    wallet
                }
            }
            currentState.copy(wallets = updatedWallets)
        }
    }

    private fun calculatePortfolioBalance() {

        var totalUsd = 0.0
        var totalSolNative = 0.0
        var totalEvmNative = 0.0
        var totalSuiNative = 0.0
        var totalTezNative = 0.0

        _state.update { currentState ->
            currentState.wallets.forEach { w ->
                totalUsd += w.balanceUSd
                when (w.wallet?.chainId) {
                    SupportedChainId.SOL -> totalSolNative += w.balanceNative
                    SupportedChainId.EVM -> totalEvmNative += w.balanceNative
                    SupportedChainId.SUI -> totalSuiNative += w.balanceNative
                    SupportedChainId.TEZ -> totalTezNative += w.balanceNative
                    else -> {}
                }
            }
            currentState.copy(
                portfolioUSdBalance = totalUsd,
                portfolioSolNativeBalance = totalSolNative,
                portfolioEvmNativeBalance = totalEvmNative,
                portfolioSuiNativeBalance = totalSuiNative,
                portfolioTezNativeBalance = totalTezNative,
                isLoading = false
            )

        }
    }


    private fun setScreenLoadingState() {
        _state.update {
            it.copy(
                isError = null,
                popupType = null,
                drawerType = null,
                isLoading = true
            )
        }
    }

}
