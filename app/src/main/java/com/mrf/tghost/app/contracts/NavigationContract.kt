package com.mrf.tghost.app.contracts

import androidx.compose.runtime.Immutable
import com.mrf.tghost.app.navigation.Routes

@Immutable
sealed class NavigationGraphEvent {
    data class NavigateTo(
        val destination: Routes,
        val popUpTo: Routes? = null,
        val inclusive: Boolean = false
    ) : NavigationGraphEvent()
    object ClearNavigation : NavigationGraphEvent()
}

data class NavigationGraphState(
    val destination: Routes? = null,
    val popUpTo: Routes? = null,
    val inclusive: Boolean = false,
    val eventTunnel: (NavigationGraphEvent) -> Unit,
)