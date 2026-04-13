package com.mrf.tghost.app.ui.screens.menuscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import com.mrf.tghost.BuildConfig
import com.mrf.tghost.R
import com.mrf.tghost.app.ui.composables.decoration.HorizontalDivider
import com.mrf.tghost.app.ui.theme.DividerDimensions.dividerExtraSmall
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingNormal
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingSmall

@Composable
fun MenuScreenFooter(
    onWebsiteClick: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(paddingNormal),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(bottom = paddingNormal)
    ) {
        TextButton(onClick = onWebsiteClick) {
            Text(
                text = "Website",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                textDecoration = TextDecoration.Underline,
            )
        }
        Text(
            text = "Illustrations by Freepik",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelSmall
        )
        HorizontalDivider(
            thickness = dividerExtraSmall,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f),
            modifier = Modifier.padding(vertical = paddingSmall)
        )
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            text = stringResource(R.string.version_template, BuildConfig.VERSION_NAME),
            textAlign = TextAlign.Center
        )
    }
}