package com.mrf.tghost.app.ui.screens.walletdetailsscreen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mrf.tghost.app.contracts.WalletState
import com.mrf.tghost.app.ui.composables.HorizontalListItem
import com.mrf.tghost.app.ui.composables.text.AnimatedGradientText
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingExtraSmall
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingSmall
import com.mrf.tghost.app.utils.extensions.smartFormatAmount

@Composable
fun WalletDetailsNetWorth(wallet: WalletState?) {

    val nativeBalanceDiff = wallet?.balanceNative?.minus(wallet.wallet?.snapshot?.balanceNative ?: 0.0) ?: 0.0
    val usdBalanceDiff = wallet?.balanceUSd?.minus(wallet.wallet?.snapshot?.balanceUSd ?: 0.0) ?: 0.0
    val hasNativeDiff = nativeBalanceDiff != 0.0
    val hasUsdDiff = usdBalanceDiff != 0.0
    val colorScheme = MaterialTheme.colorScheme

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = colorScheme.surfaceContainerLow,
        shape = MaterialTheme.shapes.large,
        tonalElevation = 2.dp
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(paddingExtraSmall),
            modifier = Modifier.padding(paddingSmall)
        ) {
            Text(
                text = "Net worth:",
                style = MaterialTheme.typography.labelMedium,
                color = colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalListItem(
                label = "${
                    wallet?.balanceNative?.smartFormatAmount(
                        2,
                        9
                    )
                } ${wallet?.wallet?.chainId}",
                value = "${
                    wallet?.balanceUSd?.smartFormatAmount(
                        2,
                        2
                    )
                } USD"
            )

            if (hasNativeDiff || hasUsdDiff) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (hasNativeDiff) {
                        val nativeColor = if (nativeBalanceDiff > 0.0) colorScheme.primary else colorScheme.error
                        Text(
                            text = formatDiff(nativeBalanceDiff),
                            style = MaterialTheme.typography.labelSmall,
                            color = nativeColor,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }

                    if (hasUsdDiff) {
                        val fiatColor = if (usdBalanceDiff > 0.0) colorScheme.primary else colorScheme.error
                        Text(
                            text = formatDiff(usdBalanceDiff),
                            style = MaterialTheme.typography.labelSmall,
                            color = fiatColor,
                            textAlign = TextAlign.End,
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }


        }
    }
}

private fun formatDiff(value: Double): String {
    val amount = value.smartFormatAmount(2, 4)
    return if (value > 0.0) "+$amount" else amount
}