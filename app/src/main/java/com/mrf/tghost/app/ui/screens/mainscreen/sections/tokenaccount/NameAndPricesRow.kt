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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingExtraSmall
import com.mrf.tghost.domain.model.TokenAccount
import com.mrf.tghost.app.utils.extensions.smartFormatAmount

@Composable
fun NameAndPricesRow(
    tokenAccount: TokenAccount
) {
    val priceNative = tokenAccount.priceNative?.toDoubleOrNull()?.smartFormatAmount(2, 4)
    val priceNativeString = priceNative?.let { "$it ${tokenAccount.quoteToken?.symbol}" }
    val priceUsd = tokenAccount.priceUsd?.toDoubleOrNull()?.smartFormatAmount(2, 4)
    
    val pricesString = if (tokenAccount.priceNative != "1" && tokenAccount.priceNative != "0" && priceNativeString != null) {
        "$priceNativeString / $priceUsd USD"
    } else {
        "$priceUsd USD"
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(paddingExtraSmall),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = tokenAccount.name ?: "",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurface,
        )

        Text(
            text = pricesString,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.End,
            maxLines = 1,
        )
    }
}
