package com.mrf.tghost.app.ui.composables.text

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.mrf.tghost.R
import com.mrf.tghost.app.ui.theme.IconDimensions.iconSmall
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingExtraSmall
import com.mrf.tghost.app.ui.theme.TGhostTheme

@Composable
fun BaseTextField(
    modifier: Modifier = Modifier,
    label: String? = null,
    value: String,
    placeholder: String = "",
    errorLabelVisibility: Boolean = false,
    errorLabelValue: String = "",
    trailingIcon: Int? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (newValue: String) -> Unit,
    onTrailingIConClick: () -> Unit = {}
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(paddingExtraSmall),
        horizontalAlignment = Alignment.Start,
    ) {
        if (label != null) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold,
                letterSpacing = TextUnit(0.15f, TextUnitType.Sp),
                modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
            )
        }

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.bodyLarge,
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            },
            trailingIcon = if (trailingIcon != null) {
                {
                    IconButton(onClick = onTrailingIConClick) {
                        Icon(
                            painter = painterResource(id = trailingIcon),
                            contentDescription = null,
                            modifier = Modifier.size(iconSmall),
                            tint = if (value.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else null,
            isError = errorLabelVisibility,
            singleLine = true,
            shape = MaterialTheme.shapes.medium,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                keyboardType = keyboardType
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                errorContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.outlineVariant,
                errorIndicatorColor = MaterialTheme.colorScheme.error
            )
        )

        AnimatedVisibility(
            visible = errorLabelVisibility,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Text(
                text = errorLabelValue,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun BaseTextFieldPreview() {
    TGhostTheme {
        Column() {
            BaseTextField(
                label = "Wallet Name",
                value = "",
                placeholder = "Enter name...",
                onValueChange = {},
                errorLabelVisibility = true,
                errorLabelValue = "Invalid name",
                trailingIcon = R.drawable.ic_edit
            )

            BaseTextField(
                label = "Wallet Name",
                value = "dfgdfg",
                placeholder = "Enter name...",
                onValueChange = {},
                errorLabelVisibility = false,
                errorLabelValue = "Invalid name",
                trailingIcon = R.drawable.ic_edit
            )
        }

    }
}
