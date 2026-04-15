package com.mrf.tghost.app.ui.composables.drawer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.mrf.tghost.R
import com.mrf.tghost.app.compositions.MainScreenEventTunnel
import com.mrf.tghost.app.contracts.MainScreenEvent
import com.mrf.tghost.app.ui.composables.RadioButtonGroup
import com.mrf.tghost.app.ui.composables.text.BaseTextField
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingSmall
import com.mrf.tghost.app.utils.isValidKey
import com.mrf.tghost.domain.model.SupportedChainId

@Composable
fun AddWalletContent() {

    val eventTunnel = MainScreenEventTunnel.current
    val supportedChains = SupportedChainId.entries.map { it.name.slice(0..2).lowercase() }
    val selectedChain = remember { mutableStateOf(supportedChains[0]) }
    val publicKeyInput = remember { mutableStateOf("") }
    val errorLabelVisibility = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(paddingSmall),
        verticalArrangement = Arrangement.spacedBy(paddingSmall)
    ) {
        RadioButtonGroup(
            options = supportedChains,
            selectedOption = selectedChain.value,
            onOptionSelected = {
                selectedChain.value = it
                errorLabelVisibility.value = false
            }
        )
        BaseTextField(
            value = publicKeyInput.value,
            onValueChange = {
                publicKeyInput.value = it
                errorLabelVisibility.value = false
            },
            placeholder = "Wallet Address (PUBLIC KEY)",
            errorLabelVisibility = errorLabelVisibility.value,
            errorLabelValue = "Invalid ${selectedChain.value} key",
            trailingIcon = R.drawable.ic_account_single,
            onTrailingIConClick = {
                val currentChainId = SupportedChainId.entries.find {
                    it.name.slice(0..2).lowercase() == selectedChain.value
                } ?: SupportedChainId.SOL

                if (publicKeyInput.value.isNotBlank() && isValidKey(publicKeyInput.value, currentChainId)) {
                    eventTunnel(
                        MainScreenEvent.ConnectWallet(
                            publicKey = publicKeyInput.value,
                            chain = currentChainId
                        )
                    )
                    eventTunnel(MainScreenEvent.CloseDrawer)
                    publicKeyInput.value = ""
                } else {
                    errorLabelVisibility.value = true
                }
            },
            keyboardType = KeyboardType.Text
        )
    }


}

