package com.mrf.tghost.app.contracts

import androidx.compose.runtime.Immutable
import com.mrf.tghost.domain.model.Wallet
import com.mrf.tghost.app.utils.enums.DrawerType
import com.mrf.tghost.app.utils.enums.PopupType

@Immutable
sealed class WalletDetailsScreenEvent {
    data class DisconnectWallet(val publicKey: String, val onDisconnectSuccess: () -> Unit) : WalletDetailsScreenEvent()
    data class UpdateWallet(val wallet: Wallet, val refreshUi: Boolean = false) : WalletDetailsScreenEvent()
    data class RefreshWallet(val publicKey: String) : WalletDetailsScreenEvent()
    data class FetchWallet(val publicKey: String) : WalletDetailsScreenEvent()
    data class OpenPopup(val popupType: PopupType) : WalletDetailsScreenEvent()
    object ClosePopup : WalletDetailsScreenEvent()
}

data class WalletDetailsScreenState(
    // ui
    val isLoading: Boolean = false,
    val isError: String? = null,
    val drawerType: DrawerType? = null,
    val popupType: PopupType? = null,
    // event tunnel
    val eventTunnel: (WalletDetailsScreenEvent) -> Unit,
    // data
    val wallet: WalletState? = null
)
