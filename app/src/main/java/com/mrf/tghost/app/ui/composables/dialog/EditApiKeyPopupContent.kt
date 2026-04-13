package com.mrf.tghost.app.ui.composables.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.mrf.tghost.R
import com.mrf.tghost.app.ui.composables.cta.BaseCta
import com.mrf.tghost.app.ui.composables.cta.TextLinkCta
import com.mrf.tghost.app.ui.composables.text.BaseTextField
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingNormal

@Composable
fun EditApiKeyPopupContent(
    providerDisplayName: String,
    value: String,
    onValueChange: (String) -> Unit,
    confirmEnabled: Boolean,
    errorMessage: String?,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(paddingNormal),
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        BaseTextField(
            label = stringResource(R.string.network_api_key_for_provider, providerDisplayName),
            value = value,
            onValueChange = onValueChange,
            placeholder = stringResource(R.string.network_api_key_placeholder),
            errorLabelVisibility = !errorMessage.isNullOrBlank(),
            errorLabelValue = errorMessage ?: "",
            trailingIcon = R.drawable.ic_edit,
            onTrailingIConClick = {},
            modifier = Modifier.fillMaxWidth(),
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextLinkCta(
                text = stringResource(R.string.cancel),
                onClick = onDismiss,
            )

            BaseCta(
                text = stringResource(R.string.confirm),
                onClick = { if (confirmEnabled) onConfirm() },
                isEnabled = confirmEnabled
            )
        }

    }
}

@Composable
@PreviewLightDark
private fun EditApiKeyPopupContentPreview() {
    EditApiKeyPopupContent(
        providerDisplayName = "Helius",
        value = "",
        onValueChange = {},
        confirmEnabled = false,
        errorMessage = null,
        onConfirm = {},
        onDismiss = {},
    )
}
