package com.mrf.tghost.app.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.mrf.tghost.R
import com.mrf.tghost.app.ui.theme.IconDimensions.iconSmall
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingNormal
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingSmall
import com.mrf.tghost.app.ui.theme.TGhostTheme

@Composable
fun ListItem(
    icon: Painter? = null,
    label: String,
    onClick: () -> Unit
) {

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(vertical = paddingNormal)
            .fillMaxWidth()
            .clickable(
                interactionSource = null,
                indication = null,
                onClick = onClick
            )
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .weight(1f)
        ) {
            if (icon != null) {
                Icon(
                    painter = icon,
                    contentDescription = stringResource(R.string.menu_item_icon),
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .size(iconSmall)
                )
            }
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = paddingSmall),
                text = label,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Icon(
            painter = painterResource(R.drawable.ic_arrow_forward),
            contentDescription = stringResource(R.string.back_icon),
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .size(iconSmall)
        )
    }
}

@Composable
fun ListItem(
    icon: Painter? = null,
    label: String,
    trailingSlot: @Composable () -> Unit = {},
    onClick: () -> Unit
) {

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(vertical = paddingNormal)
            .fillMaxWidth()
            .clickable(
                interactionSource = null,
                indication = null,
                onClick = onClick
            )
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .weight(1f)
        ) {
            if (icon != null) {
                Icon(
                    painter = icon,
                    contentDescription = stringResource(R.string.menu_item_icon),
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .size(iconSmall)
                )
            }
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = paddingSmall),
                text = label,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        trailingSlot()
    }
}

@Composable
@PreviewLightDark
private fun MenuScreenListItemPreview() {
    TGhostTheme {
        var checked by remember { mutableStateOf(true) }
        Column() {
            ListItem(
                label = "sdfgdsfgbdsffg",
                onClick = { },
                icon = painterResource(R.drawable.ic_info_round)
            )

            ListItem(
                label = "list item with switch",
                onClick = { },
                trailingSlot = {
                    Switch(
                        checked = true,
                        onCheckedChange = { },
                        thumbContent = if (checked) {
                            {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize),
                                )
                            }
                        } else {
                            null
                        }
                    )
                }
            )
        }

    }

}