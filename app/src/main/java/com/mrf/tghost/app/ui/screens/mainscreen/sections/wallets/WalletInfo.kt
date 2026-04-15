package com.mrf.tghost.app.ui.screens.mainscreen.sections.wallets

import android.content.ClipData
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mrf.tghost.R
import com.mrf.tghost.app.compositions.NavigationEventTunnel
import com.mrf.tghost.app.contracts.NavigationGraphEvent
import com.mrf.tghost.app.contracts.WalletState
import com.mrf.tghost.app.navigation.Routes
import com.mrf.tghost.app.ui.mapper.getWalletIcon
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingExtraSmall
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingSmall
import com.mrf.tghost.app.ui.theme.primaryGreen
import com.mrf.tghost.app.ui.theme.primaryRed
import com.mrf.tghost.app.utils.TWEEN_DURATION
import com.mrf.tghost.app.utils.extensions.smartFormatAmount
import com.mrf.tghost.app.utils.formatWalletDisplay
import com.mrf.tghost.domain.model.SupportedChain
import kotlinx.coroutines.launch

@Composable
fun WalletInfo(
    walletState: WalletState,
    isExpanded: Boolean,
    onItemClicked: () -> Unit
) {
    val navigationEventTunnel = NavigationEventTunnel.current
    val clipboardManager = LocalClipboard.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val transition = updateTransition(isExpanded, label = "box state")
    val color by transition.animateColor(
        transitionSpec = { tween(durationMillis = (TWEEN_DURATION * 1.5).toInt()) },
        label = "wallet info color"
    ) { expanded ->
        when (expanded) {
            true -> MaterialTheme.colorScheme.surfaceContainerHighest
            false -> MaterialTheme.colorScheme.surfaceContainerLow
        }
    }

    val nativeBalanceDiff =
        walletState.balanceNative - (walletState.wallet?.snapshot?.balanceNative ?: 0.0)
    val usdBalanceDiff = walletState.balanceUSd - (walletState.wallet?.snapshot?.balanceUSd ?: 0.0)
    val nativeBalanceSymbol = SupportedChain.entries.find { it.chain.id == walletState.wallet?.chainId }?.chain?.symbol ?: "N/A"

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(paddingSmall),
        modifier = Modifier
            .fillMaxWidth()
            .background(color)
            .padding(paddingSmall)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onItemClicked
            )
    ) {
        WalletInfoChainIcon(
            iconRes = getWalletIcon(walletState),
            onClick = {
                walletState.wallet?.publicKey?.let { pubKey ->
                    navigationEventTunnel(
                        NavigationGraphEvent.NavigateTo(
                            Routes.WalletDetailsScreen(
                                pubKey
                            )
                        )
                    )
                }
            }
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(paddingExtraSmall),
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.weight(1f)
        ) {
            // Wallet name
            Text(
                text = walletState.wallet?.name ?: "Unknown Wallet",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Address / Pubkey with Copy
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable(
                    interactionSource = null,
                    indication = null,
                    onClick = {
                        walletState.wallet?.publicKey?.let { address ->
                            scope.launch {
                                val clipData = ClipData.newPlainText("Wallet Address", address)
                                clipboardManager.setClipEntry(ClipEntry(clipData))
                                Toast.makeText(context, "Address copied", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                )
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_copy),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = formatWalletDisplay(walletState.wallet?.publicKey ?: ""),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1
                )
            }

            // Native Balance
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedContent(
                    targetState = walletState.balanceNative,
                    transitionSpec = {
                        slideInVertically { it / 2 } togetherWith fadeOut(
                            animationSpec = tween(
                                TWEEN_DURATION
                            )
                        )
                    },
                    label = "balanceNative"
                ) { balance ->
                    Text(
                        text = "${
                            balance.smartFormatAmount(
                                2,
                                6
                            )
                        } $nativeBalanceSymbol",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (nativeBalanceDiff != 0.0) {
                    Text(
                        text = "(${if (nativeBalanceDiff > 0) "+" else ""}${
                            nativeBalanceDiff.smartFormatAmount(
                                2,
                                4
                            )
                        })",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (nativeBalanceDiff > 0) primaryGreen else primaryRed
                    )
                }
            }
        }

        // USD Balance Column
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            AnimatedContent(
                targetState = walletState.balanceUSd,
                transitionSpec = {
                    slideInVertically { it / 2 } togetherWith fadeOut(
                        animationSpec = tween(
                            TWEEN_DURATION
                        )
                    )
                },
                label = "balanceUsd"
            ) { balance ->
                Text(
                    text = "${balance.smartFormatAmount(2, 2)} USD",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.End
                )
            }

            if (usdBalanceDiff != 0.0) {
                Text(
                    text = "${if (usdBalanceDiff > 0) "+" else ""}${
                        usdBalanceDiff.smartFormatAmount(
                            2,
                            2
                        )
                    } USD",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (usdBalanceDiff > 0) primaryGreen else primaryRed,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
