package com.mrf.tghost.app.ui.screens.networkscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.mrf.tghost.app.contracts.PreferencesEvent
import com.mrf.tghost.app.contracts.PreferencesState
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingNormal
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingSmall
import com.mrf.tghost.app.ui.theme.TGhostTheme
import com.mrf.tghost.app.viewmodel.PreferencesViewModel
import com.mrf.tghost.domain.model.SupportedChain

@Composable
fun NetworkScreenContent(
    modifier: Modifier = Modifier,
    preferencesViewModel: PreferencesViewModel = hiltViewModel()
) {
    val state by preferencesViewModel.state.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = paddingSmall)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(paddingNormal)
    ) {
        SupportedChain.entries.forEach { supportedChain ->
            ElevatedCard(
                modifier = Modifier.padding(horizontal = paddingSmall),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                NetworkListItem(
                    modifier = Modifier.padding(paddingNormal),
                    chain = supportedChain.chain,
                    initialPreference = state.rpcPreferences[supportedChain.chain.id],
                    rpcProviderApiKeys = state.rpcProviderApiKeys,
                    onPreferenceChanged = { preference ->
                        state.eventTunnel(
                            PreferencesEvent.SaveRpcPreference(
                                chainId = supportedChain.chain.id,
                                preference = preference
                            )
                        )
                    },
                    onSaveApiKey = { providerId, value ->
                        state.eventTunnel(
                            PreferencesEvent.SaveRpcProviderApiKey(
                                providerId,
                                value
                            )
                        )
                    }
                )
            }
        }
    }
}


@Composable
@PreviewLightDark
fun NetworkScreenContentPreview() {
    val fakeState = remember {
        mutableStateOf(
            PreferencesState(
                rpcPreferences = emptyMap(),
                rpcProviderApiKeys = emptyMap(),
                eventTunnel = {})
        )
    }
    TGhostTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(paddingNormal)
        ) {
            SupportedChain.entries.forEach { supportedChain ->
                ElevatedCard(
                    modifier = Modifier.padding(horizontal = paddingSmall),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {

                    NetworkListItem(
                        modifier = Modifier.padding(paddingNormal),
                        chain = supportedChain.chain,
                        initialPreference = fakeState.value.rpcPreferences[supportedChain.chain.id],
                        rpcProviderApiKeys = fakeState.value.rpcProviderApiKeys,
                        onPreferenceChanged = { _ -> },
                        onSaveApiKey = { _, _ -> }
                    )

                }
            }
        }
    }
}