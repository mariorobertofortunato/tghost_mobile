package com.mrf.tghost.app.compositions

import android.annotation.SuppressLint
import androidx.compose.runtime.staticCompositionLocalOf
import com.mrf.tghost.app.contracts.NavigationGraphEvent

@SuppressLint("CompositionLocalNaming")
val NavigationEventTunnel = staticCompositionLocalOf<(NavigationGraphEvent) -> Unit>{
    error("No NavigationEventTunnel provided")
}