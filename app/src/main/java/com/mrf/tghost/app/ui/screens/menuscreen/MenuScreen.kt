package com.mrf.tghost.app.ui.screens.menuscreen

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.mrf.tghost.R
import com.mrf.tghost.app.compositions.NavigationEventTunnel
import com.mrf.tghost.app.contracts.NavigationGraphEvent
import com.mrf.tghost.app.contracts.PreferencesEvent
import com.mrf.tghost.app.navigation.Routes
import com.mrf.tghost.app.ui.composables.ListItem
import com.mrf.tghost.app.ui.composables.ScreenHeader
import com.mrf.tghost.app.ui.composables.decoration.HorizontalDivider
import com.mrf.tghost.app.ui.theme.DividerDimensions.dividerExtraSmall
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingMedium
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingSmall
import com.mrf.tghost.app.utils.CONTACT_SITE
import com.mrf.tghost.app.utils.enums.MenuDrawerType
import com.mrf.tghost.app.viewmodel.PreferencesViewModel

@Composable
fun MenuScreen(
    preferencesViewModel: PreferencesViewModel = hiltViewModel()
) {
    val navigationEventTunnel = NavigationEventTunnel.current
    val context = LocalContext.current
    val state by preferencesViewModel.state.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            ScreenHeader(
                headerTextValue = stringResource(R.string.menu_screen_header_value),
                onBackPressed = {
                    navigationEventTunnel(
                        NavigationGraphEvent.NavigateTo(
                            destination = Routes.MainScreen,
                            popUpTo = Routes.MenuScreen,
                            inclusive = true
                        )
                    )
                },
                modifier = Modifier.statusBarsPadding()
            )
        },
        bottomBar = {
            MenuScreenFooter(
                onWebsiteClick = {
                    context.startActivity(Intent(Intent.ACTION_VIEW, CONTACT_SITE.toUri()))
                }
            )
        }
    ) { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(paddingSmall),
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = paddingMedium)
                .padding(top = paddingSmall)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            ListItem(
                label = "Live update",
                onClick = { },
                trailingSlot = {
                    Switch(
                        checked = state.liveUpdateEnabled,
                        onCheckedChange = {
                            state.eventTunnel(PreferencesEvent.ToggleLiveUpdate)
                        },
                        thumbContent = if (state.liveUpdateEnabled) {
                            {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize),
                                )
                            }
                        } else {
                            null
                        }
                    )
                }
            )
            HorizontalDivider(
                thickness = dividerExtraSmall,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
            )
            ListItem(
                label = stringResource(R.string.menu_item_privacy_and_terms),
                onClick = {
                    //preferencesViewModel.openDrawer(MenuDrawerType.PrivacyAndTerms)
                },
                icon = painterResource(R.drawable.ic_terms_of_service)
            )
        }
    }
}
