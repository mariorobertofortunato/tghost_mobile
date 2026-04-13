package com.mrf.tghost.app.ui.composables.cta

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.mrf.tghost.R
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingNormal

@Composable
fun ActionButtons(
    confirmCtaText: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
    ) {
        TextLinkCta(
            icon = painterResource(R.drawable.ic_close),
            onClick = onDismiss,
            modifier = Modifier
                .padding(paddingNormal)
        )

        BaseCta(
            text = confirmCtaText,
            icon = painterResource(R.drawable.ic_confirm),
            onClick = onConfirm,
            modifier = Modifier
                .weight(1f)
        )
    }
}

@Composable
@Preview
private fun ActionButtonsPreview() {
    ActionButtons(
        onDismiss = { Unit },
        onConfirm = { Unit },
        confirmCtaText = "Confirm"
    )
}