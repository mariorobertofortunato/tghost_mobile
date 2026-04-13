package com.mrf.tghost.app.ui.screens.mainscreen.sections.tokenaccount

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingExtraSmall
import com.mrf.tghost.domain.model.TokenAccount
import com.mrf.tghost.app.utils.extensions.smartFormatAmount

@Composable
fun TokenAmountAndValuesRow(
    tokenAccount: TokenAccount
) {
    val decimals = tokenAccount.decimals ?: 18
    val maxDecimals = maxOf(4, minOf(decimals, 6))
    val uiAmount = tokenAccount.amountDouble?.smartFormatAmount(2, maxDecimals)
    val valueNative = tokenAccount.valueNative?.smartFormatAmount(2, 2)
    val valueUSd = tokenAccount.valueUsd?.smartFormatAmount(2, 2)

    val valueNativeString = if (tokenAccount.priceNative != "1" && tokenAccount.priceNative != "0") {
        "$valueNative ${tokenAccount.quoteToken?.symbol} / "
    } else {
        null
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(paddingExtraSmall),
        verticalAlignment = Alignment.CenterVertically, // Changed to Center for better alignment with larger titles
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "$uiAmount ${tokenAccount.symbol}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(paddingExtraSmall),
            verticalAlignment = Alignment.CenterVertically
        ){
            if (valueNativeString != null) {
                Text(
                    text = valueNativeString,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                )
            }

            Text(
                text = "$valueUSd USD",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
