package com.mrf.tghost.app.ui.composables.cta

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.innerShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingNormal
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingSmall
import com.mrf.tghost.app.ui.theme.TGhostTheme
import com.mrf.tghost.app.ui.theme.primaryBlack

@Composable
fun BigTextCta(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    text: String,
    icon: ImageVector? = null,
    textColor: Color = MaterialTheme.colorScheme.onPrimary,
    background: Color = MaterialTheme.colorScheme.primaryContainer,
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(
                color = background,
                shape = RoundedCornerShape(paddingSmall)
            )
            .innerShadow(
                shape = RoundedCornerShape(paddingSmall),
                shadow = Shadow(
                    radius = 3.dp,
                    spread = 1.dp,
                    color = primaryBlack.copy(alpha = 0.75f),
                    offset = DpOffset(x = 0.dp, 4.dp)
                )
            )
            .clickable {
                onClick()
            }
            .padding(paddingNormal)

    ) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(paddingSmall),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge,
                color = textColor,
                fontWeight = FontWeight.Black,
                letterSpacing = TextUnit(
                    (0.5f * MaterialTheme.typography.titleSmall.fontSize.value),
                    TextUnitType.Sp
                ),
                modifier = Modifier,
            )

            if (icon != null) {
                Icon(
                    imageVector = icon,
                    tint = textColor,
                    contentDescription = text,
                )
            }
        }


    }
}

@Composable
@PreviewLightDark
fun BigTextCtaPreview() {
    TGhostTheme {
        BigTextCta(
            text = "add wallet",
            onClick = {

            },
            icon = Icons.Outlined.Check
        )
    }

}