package com.mrf.tghost.app.ui.screens.networkscreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mrf.tghost.R
import com.mrf.tghost.app.ui.composables.LargeDropdownMenu
import com.mrf.tghost.app.ui.composables.RadioButtonGroup
import com.mrf.tghost.app.ui.composables.dialog.EditApiKeyPopupContent
import com.mrf.tghost.app.ui.composables.dialog.PopupWrapper
import com.mrf.tghost.app.ui.composables.text.TextComponent
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingSmall
import com.mrf.tghost.app.ui.theme.TGhostTheme
import com.mrf.tghost.domain.model.Chain
import com.mrf.tghost.domain.model.NetworkType
import com.mrf.tghost.domain.model.RpcPreference
import com.mrf.tghost.domain.model.RpcProviderId
import com.mrf.tghost.domain.model.RpcProviderOption
import com.mrf.tghost.domain.model.SupportedChain


@Composable
fun NetworkListItem(
    modifier: Modifier = Modifier,
    chain: Chain,
    initialPreference: RpcPreference?,
    rpcProviderApiKeys: Map<String, String>,
    onPreferenceChanged: (RpcPreference) -> Unit,
    onSaveApiKey: (RpcProviderId, String) -> Unit,
) {
    val allProviders: List<RpcProviderOption> = chain.rpcProviders

    val selectedProviderIndex = if (allProviders.isEmpty()) -1
    else allProviders.indexOfFirst { it.id == initialPreference?.providerId }.coerceIn(0, allProviders.size - 1)
    val selectedProvider = allProviders.getOrNull(selectedProviderIndex)

    val savedApiKey = selectedProvider?.let { rpcProviderApiKeys[it.id.name]?.takeIf { k -> k.isNotBlank() } }

    val supportedNetworksForProvider = selectedProvider?.supportedNetworks ?: emptyList()
    val supportedNetworkNames = supportedNetworksForProvider.map { it.name }.toSet()
    val selectedNetwork = when {
        initialPreference == null -> supportedNetworksForProvider.firstOrNull() ?: NetworkType.MAINNET
        initialPreference.networkType in supportedNetworksForProvider -> initialPreference.networkType
        else -> supportedNetworksForProvider.firstOrNull() ?: NetworkType.MAINNET
    }
    val allNetworkOptionLabels = NetworkType.entries.map { it.name }

    var showApiKeyDialog by remember { mutableStateOf(false) }
    var pendingPreference by remember { mutableStateOf<RpcPreference?>(null) }
    var apiKeyDialogValue by remember { mutableStateOf("") }

    val providerForDialog = pendingPreference?.providerId?.let { id ->
        allProviders.find { it.id == id }
    } ?: selectedProvider

    if (showApiKeyDialog && providerForDialog != null) {
        val isNewSelection = pendingPreference != null
        val apiKeyTrimmed = apiKeyDialogValue.trim()
        val isApiKeyValid = apiKeyTrimmed.isNotBlank()
        val errorMessage = if (!isApiKeyValid && apiKeyDialogValue.isNotEmpty())
            stringResource(R.string.network_api_key_error_empty) else null
        PopupWrapper(
            hasDismissIcon = true,
            onDismiss = {
                showApiKeyDialog = false
                pendingPreference = null
            },
            content = {
                EditApiKeyPopupContent(
                    providerDisplayName = providerForDialog.displayName,
                    value = apiKeyDialogValue,
                    onValueChange = { apiKeyDialogValue = it },
                    confirmEnabled = isApiKeyValid,
                    errorMessage = errorMessage,
                    onConfirm = {
                        onSaveApiKey(providerForDialog.id, apiKeyTrimmed)
                        pendingPreference?.let { onPreferenceChanged(it) }
                        pendingPreference = null
                        showApiKeyDialog = false
                    },
                    onDismiss = {
                        showApiKeyDialog = false
                        pendingPreference = null
                    },
                )
            }
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(paddingSmall),
        modifier = modifier//.padding(horizontal = paddingSmall)
    ) {
        if (allProviders.isNotEmpty()) {
            LargeDropdownMenu(
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(R.string.network_rpc_provider_label, chain.name),
                items = allProviders.map { it.displayName },
                selectedIndex = selectedProviderIndex,
                onItemSelected = { index, _ ->
                    val newProvider = allProviders.getOrNull(index) ?: return@LargeDropdownMenu
                    val newPreference = RpcPreference(
                        providerId = newProvider.id,
                        networkType = newProvider.supportedNetworks.firstOrNull() ?: NetworkType.MAINNET
                    )
                    if (newProvider.requiresApiKey) {
                        val keyForNew = rpcProviderApiKeys[newProvider.id.name]?.takeIf { it.isNotBlank() }
                        if (keyForNew == null) {
                            pendingPreference = newPreference
                            apiKeyDialogValue = ""
                            showApiKeyDialog = true
                        } else {
                            onPreferenceChanged(newPreference)
                        }
                    } else {
                        onPreferenceChanged(newPreference)
                    }
                },
                selectedItemToString = { it }
            )
        } else {
            Text(
                text = stringResource(R.string.network_no_provider_available),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (selectedProvider != null) {
            RadioButtonGroup(
                options = allNetworkOptionLabels,
                selectedOption = selectedNetwork.name,
                optionTextStyle = MaterialTheme.typography.labelSmall,
                onOptionSelected = { optionName ->
                    val newNetwork = NetworkType.valueOf(optionName)
                    val provider = allProviders.getOrNull(selectedProviderIndex) ?: return@RadioButtonGroup
                    onPreferenceChanged(RpcPreference(providerId = provider.id, networkType = newNetwork))
                },
                optionEnabled = { optionName -> optionName in supportedNetworkNames },
            )
        }

        if (selectedProvider?.requiresApiKey == true) {
            Spacer(modifier = Modifier.padding(top = paddingSmall))
            if (savedApiKey != null) {
                Row(
                    modifier = Modifier
                        .align(Alignment.Start)
                        .clickable {
                            pendingPreference = null
                            apiKeyDialogValue = savedApiKey
                            showApiKeyDialog = true
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(paddingSmall)
                ) {
                    TextComponent(
                        text = stringResource(R.string.network_edit_api_key_label),
                        textStyle = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Icon(
                        painter = painterResource(R.drawable.ic_edit),
                        contentDescription = stringResource(R.string.edit),
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}
@Composable
@Preview(showBackground = true)
fun NetworkListItemPreview() {
    TGhostTheme {
        NetworkListItem(
            chain = SupportedChain.SOLANA.chain,
            initialPreference = null,
            rpcProviderApiKeys = emptyMap(),
            onPreferenceChanged = {},
            onSaveApiKey = { _, _ -> }
        )
    }

}