package com.mrf.tghost.app.ui.screens.walletdetailsscreen

import android.content.ClipData
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mrf.tghost.R
import com.mrf.tghost.app.compositions.WalletDetailsEventTunnel
import com.mrf.tghost.app.contracts.WalletDetailsScreenEvent
import com.mrf.tghost.app.contracts.WalletState
import com.mrf.tghost.app.ui.composables.HorizontalListItem
import com.mrf.tghost.app.ui.composables.text.BaseTextField
import com.mrf.tghost.app.ui.mapper.getWalletIcon
import com.mrf.tghost.app.ui.screens.mainscreen.sections.wallets.WalletInfoChainIcon
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingExtraSmall
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingNormal
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingSmall
import com.mrf.tghost.app.utils.formatWalletDisplay
import com.mrf.tghost.domain.model.SupportedChainId
import com.mrf.tghost.domain.model.Wallet
import kotlinx.coroutines.launch

@Composable
fun WalletDetailsInfo(wallet: WalletState?) {

    val clipboardManager = LocalClipboard.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val eventTunnel = WalletDetailsEventTunnel.current
    val walletNameInput = remember { mutableStateOf("${wallet?.wallet?.name}") }
    val walletNameEditVisibility = remember { mutableStateOf(false) }
    val errorLabelVisibility = remember { mutableStateOf(false) }
    val colorScheme = MaterialTheme.colorScheme

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        color = colorScheme.surfaceContainerLow,
        shape = MaterialTheme.shapes.large,
        tonalElevation = 2.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(paddingNormal),
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingSmall)
        ) {
            WalletInfoChainIcon(
                iconRes = getWalletIcon(wallet ?: return@Row),
                onClick = { }
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(paddingExtraSmall),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                HorizontalListItem(
                    label = "Wallet name:",
                    value = wallet.wallet?.name ?: "?NAME?",
                    valueIconRes = R.drawable.ic_edit,
                    onValueClick = {
                        walletNameEditVisibility.value = !walletNameEditVisibility.value
                    }
                )

                AnimatedVisibility(
                    visible = walletNameEditVisibility.value,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    BaseTextField(
                        value = walletNameInput.value,
                        onValueChange = {
                            walletNameInput.value = it
                            errorLabelVisibility.value = false
                        },
                        placeholder = "Insert wallet name",
                        errorLabelVisibility = errorLabelVisibility.value,
                        errorLabelValue = "Please insert a value",
                        trailingIcon = R.drawable.ic_confirm,
                        onTrailingIConClick = {
                            if (walletNameInput.value.isNotBlank()) {
                                eventTunnel(
                                    WalletDetailsScreenEvent.UpdateWallet(
                                        wallet =
                                            Wallet(
                                                publicKey = wallet.wallet?.publicKey ?: "",
                                                name = walletNameInput.value,
                                                chainId = wallet.wallet?.chainId
                                                    ?: SupportedChainId.SOL,
                                                snapshot = wallet.wallet?.snapshot
                                            ),
                                        refreshUi = true
                                    )
                                )
                                walletNameEditVisibility.value = false
                            } else {
                                errorLabelVisibility.value = true
                            }
                        },
                        keyboardType = KeyboardType.Text
                    )
                }

                HorizontalListItem(
                    label = "Pubkey:",
                    value = formatWalletDisplay(wallet.wallet?.publicKey ?: ""),
                    valueIconRes = R.drawable.ic_copy,
                    onValueClick = {
                        val address = wallet.wallet?.publicKey ?: ""
                        if (address.isNotEmpty()) {
                            scope.launch {
                                val clipData =
                                    ClipData.newPlainText("Wallet Address", address)
                                clipboardManager.setClipEntry(ClipEntry(clipData))
                                Toast.makeText(
                                    context,
                                    "Address copied",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                )
            }
        }
    }
}