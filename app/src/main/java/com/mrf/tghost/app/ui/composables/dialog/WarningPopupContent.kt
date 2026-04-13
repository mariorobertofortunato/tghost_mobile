package com.mrf.tghost.app.ui.composables.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mrf.tghost.R
import com.mrf.tghost.app.ui.composables.cta.BaseCta
import com.mrf.tghost.app.ui.composables.cta.TextLinkCta
import com.mrf.tghost.app.ui.composables.decoration.HorizontalSpacer
import com.mrf.tghost.app.ui.theme.IconDimensions.iconBig
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingNormal
import com.mrf.tghost.app.ui.theme.TGhostTheme

@Composable
fun WarningPopupContent(
    title: String? = null,
    body: String? = null,
    dismissCtaString: String? = null,
    confirmCtaString: String? = null,
    onConfirm: () -> Unit = {},
    onDismiss: () -> Unit = {},
    content: @Composable () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(paddingNormal),
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_warning),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(iconBig)
        )

        if (title != null) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (body != null) {
            Text(
                text = body,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        HorizontalSpacer(height = 1.dp)

        content()

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (dismissCtaString != null) {
                TextLinkCta(
                    text = dismissCtaString,
                    onClick = onDismiss,
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            BaseCta(
                text = confirmCtaString ?: stringResource(R.string.ok),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                 ,
                onClick = onConfirm
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun WarningPopupContentPreview() {
    TGhostTheme {
        WarningPopupContent(
            title = "Confirm Action",
            body = "Are you sure you want to perform this destructive action?",
            dismissCtaString = "Cancel",
            confirmCtaString = "Delete",
            onConfirm = {}
        )
    }
}
