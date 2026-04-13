package com.mrf.tghost.app.ui.screens.walletdetailsscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.mrf.tghost.R
import com.mrf.tghost.app.compositions.NavigationEventTunnel
import com.mrf.tghost.app.compositions.WalletDetailsEventTunnel
import com.mrf.tghost.app.contracts.NavigationGraphEvent
import com.mrf.tghost.app.contracts.WalletDetailsScreenEvent
import com.mrf.tghost.app.navigation.Routes
import com.mrf.tghost.app.ui.composables.ScreenHeader
import com.mrf.tghost.app.ui.composables.dialog.PopupWrapper
import com.mrf.tghost.app.ui.composables.dialog.WarningPopupContent
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingSmall
import com.mrf.tghost.app.utils.enums.PopupType
import com.mrf.tghost.app.utils.enums.WarningType
import com.mrf.tghost.app.viewmodel.WalletDetailsViewModel

@Composable
fun WalletDetailsScreen(
    walletDetailsViewModel: WalletDetailsViewModel = hiltViewModel(),
    walletId: String?
) {
    val navigationEventTunnel = NavigationEventTunnel.current
    val state by walletDetailsViewModel.state.collectAsState()

    CompositionLocalProvider(WalletDetailsEventTunnel provides state.eventTunnel) {
        LaunchedEffect(Unit) {
            state.eventTunnel(WalletDetailsScreenEvent.FetchWallet(walletId ?: ""))
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.surface,
            topBar = {
                ScreenHeader(
                    modifier = Modifier.statusBarsPadding(),
                    headerTextValue = state.wallet?.wallet?.name ?: "",
                    onBackPressed = {
                        navigationEventTunnel(
                            NavigationGraphEvent.NavigateTo(
                                destination = Routes.MainScreen,
                                popUpTo = Routes.WalletDetailsScreen(walletId = walletId),
                                inclusive = true
                            )
                        )
                    },
                    actionIcon = Icons.Outlined.Refresh,
                    onActionPressed = {
                        state.eventTunnel(WalletDetailsScreenEvent.RefreshWallet(walletId ?: ""))
                    }
                )
            },
            bottomBar = {
                TextButton(
                    onClick = {
                        state.eventTunnel(WalletDetailsScreenEvent.OpenPopup(PopupType.Warning(WarningType.DeleteWallet)))
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    contentPadding = PaddingValues(paddingSmall),
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Disconnect wallet".uppercase(),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = TextUnit(0.1f, TextUnitType.Sp)
                        )
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                if (state.wallet?.wallet != null) {
                    WalletDetailsScreenContent(state.wallet)
                }
            }
        }

        // Popups
        when (state.popupType) {
            is PopupType.Warning -> {
                val warningType = (state.popupType as PopupType.Warning).type
                if (warningType == WarningType.DeleteWallet) {
                    PopupWrapper(
                        onDismiss = { state.eventTunnel(WalletDetailsScreenEvent.ClosePopup) },
                        content = {
                            WarningPopupContent(
                                title = "Disconnect Wallet",
                                body = "Are you sure you want to disconnect this wallet? You will need to add it again manually to track it.",
                                confirmCtaString = "Disconnect",
                                dismissCtaString = stringResource(R.string.cancel),
                                onConfirm = {
                                    state.eventTunnel(
                                        WalletDetailsScreenEvent.DisconnectWallet(
                                            publicKey = walletId ?: "",
                                            onDisconnectSuccess = {
                                                state.eventTunnel(WalletDetailsScreenEvent.ClosePopup)
                                                navigationEventTunnel(
                                                    NavigationGraphEvent.NavigateTo(
                                                        destination = Routes.MainScreen,
                                                        popUpTo = Routes.WalletDetailsScreen(walletId = walletId),
                                                        inclusive = true
                                                    )
                                                )
                                            }
                                        )
                                    )
                                },
                                onDismiss = { state.eventTunnel(WalletDetailsScreenEvent.ClosePopup) }
                            )
                        }
                    )
                }
            }
            else -> {}
        }
    }
}
