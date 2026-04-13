package com.mrf.tghost.app.ui.composables.cta

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.mrf.tghost.R
import com.mrf.tghost.app.ui.composables.text.TextComponent
import com.mrf.tghost.app.ui.theme.BorderDimensions.borderSmall
import com.mrf.tghost.app.ui.theme.IconDimensions.iconSmall
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingNormal
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingSmall
import com.mrf.tghost.app.ui.theme.shapes
import com.mrf.tghost.app.utils.enums.LetterSpacing
import com.mrf.tghost.app.utils.enums.TextSize

@Deprecated("basta usare BaseCta con modifier.fillMaxWidth()")
@Composable
fun BaseCtaFullWidth(
    text: String? = null,
    textSize: TextSize? = TextSize.CALLOUT,
    icon: Painter? = null,
    iconSize: Dp? = iconSmall,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    color: Color = MaterialTheme.colorScheme.primaryContainer,
    onClick: () -> Unit,
    hasBorder: Boolean? = false,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = borderSmall,
                color = if (hasBorder == true) MaterialTheme.colorScheme.onPrimaryContainer else Color.Transparent,
                shape = shapes.extraLarge
            )
            .background(
                color = color,
                shape = shapes.extraLarge
            )
            .padding(paddingNormal)
            .clickable(
                indication = null,
                interactionSource = null,
                onClick = onClick
            )
    ) {
        if (icon != null) {
            Icon(
                painter = icon,
                tint = contentColor,
                contentDescription = "Call to action $text",
                modifier = Modifier
                    .padding(end = paddingSmall)
                    .size(iconSize ?: iconSmall)
            )
        }

        if (text != null) {
            TextComponent(
                text = text.uppercase(),
                textSize = textSize ?: TextSize.CALLOUT,
                color = contentColor,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = LetterSpacing.FIFTEEN,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f, false)
            )
        }
    }
}

@Composable
@Preview
private fun BaseCtaFullWidthPreview() {
    Column(
        verticalArrangement = Arrangement.spacedBy(paddingNormal),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        BaseCtaFullWidth(
            text = "Esempio CTA",
            textSize = TextSize.CAPTION,
            onClick = {}
        )
        BaseCtaFullWidth(
            icon = painterResource(R.drawable.ic_arrow_square_up),
            onClick = {}
        )
        BaseCtaFullWidth(
            text = "Esempio CTA",
            icon = painterResource(R.drawable.ic_arrow_square_up),
            onClick = {},
            iconSize = iconSmall
        )
    }

}