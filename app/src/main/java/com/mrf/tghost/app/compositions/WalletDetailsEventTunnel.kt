package com.mrf.tghost.app.compositions

import android.annotation.SuppressLint
import androidx.compose.runtime.staticCompositionLocalOf
import com.mrf.tghost.app.contracts.WalletDetailsScreenEvent

@SuppressLint("CompositionLocalNaming")
val WalletDetailsEventTunnel = staticCompositionLocalOf<(WalletDetailsScreenEvent) -> Unit>{
    error("No WalletDetailsEventTunnel provided")
}