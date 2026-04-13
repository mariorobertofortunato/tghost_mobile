package com.mrf.tghost.app.ui.composables.cta

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.mrf.tghost.R
import com.mrf.tghost.app.ui.theme.IconDimensions.iconSmall
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingNormal
import com.mrf.tghost.app.ui.theme.TGhostTheme

@Composable
fun TextLinkCta(
    modifier: Modifier = Modifier,
    text: String? = null,
    icon: Painter? = null,
    isEnabled: Boolean = true,
    onClick: () -> Unit = {},
) {
    TextButton(
        onClick = onClick,
        modifier = modifier
            .defaultMinSize(minHeight = 40.dp),
        enabled = isEnabled,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        content = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != null) {
                    Icon(
                        painter = icon,
                        contentDescription = null,
                        modifier = Modifier.size(iconSmall)
                    )
                }
                if (text != null) {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }
    )
}

@Composable
@PreviewLightDark
private fun TextLinkCtaPreview() {
    TGhostTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(paddingNormal),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            TextLinkCta(
                text = "Esempio CTA",
                onClick = {},
            )
            TextLinkCta(
                text = "Esempio CTA",
                icon = painterResource(R.drawable.ic_arrow_square_up),
                onClick = {},
            )
        }
    }
}
