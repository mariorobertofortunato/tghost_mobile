package com.mrf.tghost.app.contracts

import androidx.compose.runtime.Immutable
import com.mrf.tghost.app.utils.enums.DrawerType
import com.mrf.tghost.app.utils.enums.PopupType
import com.mrf.tghost.domain.model.SupportedChainId

@Immutable
sealed class MainScreenEvent {
    object FetchWallets : MainScreenEvent()
    data class ConnectWallet(val publicKey: String, val chain: SupportedChainId) : MainScreenEvent()
    data class OpenDrawer(val drawerType: DrawerType, val data: Any? = null) : MainScreenEvent()
    object CloseDrawer : MainScreenEvent()
    data class OpenPopup(val popupType: PopupType) : MainScreenEvent()
    object ClosePopup : MainScreenEvent()
    object OnDispose : MainScreenEvent()
}

data class MainScreenState(
    // ui
    val isLoading: Boolean = false,
    val isError: String? = null,
    val drawerType: DrawerType? = null,
    val popupType: PopupType? = null,
    // event tunnel
    val eventTunnel: (MainScreenEvent) -> Unit,
    // data
    val wallets: List<WalletState> = emptyList(),
    val selectedWallet: WalletState? = null,
    val portfolioSolNativeBalance: Double = 0.0,
    val portfolioEvmNativeBalance: Double = 0.0,
    val portfolioSuiNativeBalance: Double = 0.0,
    val portfolioTezNativeBalance: Double = 0.0,
    val portfolioUSdBalance: Double = 0.0
)
