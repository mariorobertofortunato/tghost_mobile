package com.mrf.tghost.app.compositions

import android.annotation.SuppressLint
import androidx.compose.runtime.staticCompositionLocalOf
import com.mrf.tghost.app.contracts.MainScreenEvent

@SuppressLint("CompositionLocalNaming")
val MainScreenEventTunnel = staticCompositionLocalOf<(MainScreenEvent) -> Unit>{
    error("No MainScreenEventTunnel provided")
}