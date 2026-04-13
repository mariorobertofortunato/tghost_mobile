package com.mrf.tghost.app.ui.screens.networkscreen

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mrf.tghost.R
import com.mrf.tghost.app.compositions.NavigationEventTunnel
import com.mrf.tghost.app.contracts.NavigationGraphEvent
import com.mrf.tghost.app.navigation.Routes
import com.mrf.tghost.app.ui.composables.ScreenHeader

@Composable
fun NetworkScreen() {

    val navigationEventTunnel = NavigationEventTunnel.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            ScreenHeader(
                headerTextValue = stringResource(R.string.network_screen_header_value),
                onBackPressed = {
                    navigationEventTunnel(
                        NavigationGraphEvent.NavigateTo(
                            destination = Routes.MainScreen,
                            popUpTo = Routes.NetworkScreen,
                            inclusive = true
                        )
                    )
                },
                modifier = Modifier
                    .statusBarsPadding()
            )
        }
    ) { paddingValues ->

        NetworkScreenContent(
            modifier = Modifier
                .padding(paddingValues)
        )

    }
}
