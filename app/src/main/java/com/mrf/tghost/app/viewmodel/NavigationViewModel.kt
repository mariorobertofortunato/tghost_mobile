package com.mrf.tghost.app.viewmodel

import androidx.lifecycle.ViewModel
import com.mrf.tghost.app.contracts.NavigationGraphEvent
import com.mrf.tghost.app.contracts.NavigationGraphState
import com.mrf.tghost.app.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(
        NavigationGraphState(
            eventTunnel = { event ->
                processEvent(event)
            }
        )
    )
    val state: StateFlow<NavigationGraphState> = _state.asStateFlow()

    private fun processEvent(event: NavigationGraphEvent) {
        when (event) {
            is NavigationGraphEvent.NavigateTo -> navigateTo(event.destination, event.popUpTo, event.inclusive)
            is NavigationGraphEvent.ClearNavigation -> clearNavigation()
        }
    }

    private fun navigateTo(route: Routes, popUpTo: Routes? = null, inclusive: Boolean = false) {
        _state.update { it.copy(destination = route, popUpTo = popUpTo, inclusive = inclusive) }
    }

    private fun clearNavigation() {
        _state.update { it.copy(destination = null, popUpTo = null, inclusive = false) }
    }

}