package com.mrf.tghost.app.ui.composables.cta

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingExtraSmall
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingSmall
import com.mrf.tghost.app.ui.theme.TGhostTheme

@Composable
fun SmallTextCta(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    text: String,
    icon: ImageVector?= null,
    color: Color = MaterialTheme.colorScheme.onPrimary
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(paddingSmall),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(paddingSmall))
            .clickable {
                onClick()
            }
            .padding(paddingExtraSmall, paddingSmall )

    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall,
            color = color,
            fontWeight = FontWeight.Black,
            letterSpacing = TextUnit((0.5f * MaterialTheme.typography.titleSmall.fontSize.value), TextUnitType.Sp),
        )

        if (icon != null) {
            Icon(
                imageVector = icon,
                tint = color,
                contentDescription = text,
            )
        }

    }
}

@Composable
@PreviewLightDark
fun SmallTextCtaPreview(){
    TGhostTheme {
        SmallTextCta(
            text = "add wallet",
            onClick = {

            },
            icon = Icons.Outlined.Check
        )
    }

}