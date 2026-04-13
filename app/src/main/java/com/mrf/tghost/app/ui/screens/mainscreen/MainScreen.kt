package com.mrf.tghost.app.ui.screens.mainscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.mrf.tghost.R
import com.mrf.tghost.app.compositions.MainScreenEventTunnel
import com.mrf.tghost.app.compositions.NavigationEventTunnel
import com.mrf.tghost.app.contracts.MainScreenEvent
import com.mrf.tghost.app.contracts.NavigationGraphEvent
import com.mrf.tghost.app.navigation.Routes
import com.mrf.tghost.app.ui.composables.dialog.PopupWrapper
import com.mrf.tghost.app.ui.composables.dialog.WarningPopupContent
import com.mrf.tghost.app.ui.composables.drawer.AddWalletContent
import com.mrf.tghost.app.ui.composables.drawer.BottomSheetWrapper
import com.mrf.tghost.app.utils.enums.DrawerType
import com.mrf.tghost.app.utils.enums.PopupType
import com.mrf.tghost.app.viewmodel.MainScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    mainScreenViewModel: MainScreenViewModel = hiltViewModel(),
) {
    val navigationEventTunnel = NavigationEventTunnel.current
    val state by mainScreenViewModel.state.collectAsState()

    CompositionLocalProvider(MainScreenEventTunnel provides state.eventTunnel) {

        LaunchedEffect(Unit) {
            state.eventTunnel(MainScreenEvent.FetchWallets)
        }

        // Needed when Live Update is enabled,
        // in order to cancel the fetchJob when navigating away
        // (the viemodelscope is not killed as the viewmodel would stay in the back stack)
        DisposableEffect(Unit) {
            onDispose {
                state.eventTunnel(MainScreenEvent.OnDispose)
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.surface)
                .then(
                    if (state.popupType != null) {
                        Modifier.blur(8.dp)
                    } else {
                        Modifier
                    }
                )
        ) {
            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    MainScreenHeader(
                        onNetworkIconClick = {
                            navigationEventTunnel(NavigationGraphEvent.NavigateTo(Routes.NetworkScreen))
                        },
                        onMenuIconClick = {
                            navigationEventTunnel(NavigationGraphEvent.NavigateTo(Routes.MenuScreen))
                        },
                        modifier = Modifier
                            .statusBarsPadding()
                    )
                },
                floatingActionButton = {
                    ExtendedFloatingActionButton(
                        onClick = {
                            state.eventTunnel(MainScreenEvent.OpenDrawer(DrawerType.AddWallet))
                        },
                        icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                        text = { Text(text = stringResource(R.string.add_account)) },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            //.navigationBarsPadding()
                    )
                }
            ) { paddingValues ->
                MainScreenContent(
                    state = state,
                    modifier = Modifier.padding(paddingValues)
                )
            }

            // Drawers and Popups...
            when (state.drawerType) {
                DrawerType.AddWallet -> {
                    BottomSheetWrapper(
                        sheetState = rememberModalBottomSheetState(),
                        headerValue = stringResource(R.string.add_account),
                        headerIcon = painterResource(R.drawable.ic_account_single),
                        onDismiss = { state.eventTunnel(MainScreenEvent.CloseDrawer) },
                        content = { AddWalletContent() }
                    )
                }
                else -> {}
            }

            when (state.popupType) {
                is PopupType.Error -> {
                    PopupWrapper(
                        content = {
                            WarningPopupContent(
                                title = stringResource(R.string.error_popup_title),
                                body = "An error occurred",
                                onConfirm = { state.eventTunnel(MainScreenEvent.ClosePopup) }
                            )
                        },
                        onDismiss = { state.eventTunnel(MainScreenEvent.ClosePopup) }
                    )
                }
                else -> {}
            }
        }
    }
}
