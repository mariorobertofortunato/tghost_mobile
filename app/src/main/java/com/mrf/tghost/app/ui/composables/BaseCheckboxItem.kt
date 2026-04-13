package com.mrf.tghost.app.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.mrf.tghost.R
import com.mrf.tghost.app.ui.composables.text.TextComponent
import com.mrf.tghost.app.ui.theme.IconDimensions.iconExtraSmall
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingSmall
import com.mrf.tghost.app.utils.enums.TextSize


@Composable
fun BaseCheckboxItem(
    label: String,
    icon: Painter? = null,
    isChecked: Boolean,
    onTrailingIconClick: () -> Unit,
    onCheckedChange: (checkBoxStatus: Boolean) -> Unit
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(paddingSmall),
        modifier = Modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = isChecked,
                onCheckedChange = {
                    onCheckedChange(it)
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.onPrimary,
                    uncheckedColor = MaterialTheme.colorScheme.onPrimary,
                    checkmarkColor = MaterialTheme.colorScheme.primary
                )
            )
            TextComponent(
                text = label,
                textSize = TextSize.BODY
            )

        }
        if (icon != null) {
            Icon(
                painter = icon,
                contentDescription = stringResource(R.string.checkbox_item_trailing_icon),
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .padding(end = paddingSmall)
                    .size(iconExtraSmall)
                    .clickable(
                        onClick = onTrailingIconClick,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    )

            )
        }

    }
}

@Composable
@Preview
private fun NewTransactionScreenExtraBudgetFieldPreview() {
    BaseCheckboxItem(
        label = "EXTRA BUDGET",
        isChecked = false,
        onCheckedChange = { _ -> Unit },
        icon = painterResource(R.drawable.ic_help),
        onTrailingIconClick = { }
    )
}