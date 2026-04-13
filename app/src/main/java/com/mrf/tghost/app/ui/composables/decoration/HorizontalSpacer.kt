package com.mrf.tghost.app.ui.composables.decoration

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.innerShadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingMedium
import com.mrf.tghost.app.ui.theme.primaryBlack
import com.mrf.tghost.app.ui.theme.secondaryGreen

@Composable
fun HorizontalSpacer(
    modifier: Modifier = Modifier,
    height: Dp = paddingMedium,
    backgroundColor: Color = MaterialTheme.colorScheme.primary
) {
    Spacer(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(backgroundColor)
            .innerShadow(
                shape = RoundedCornerShape(0),
                shadow = Shadow(
                    radius = 3.dp,
                    spread = 1.dp,
                    color = primaryBlack.copy(alpha = 0.75f),
                    offset = DpOffset(x = 0.dp, 4.dp)
                )
            )
    )
}

@Composable
fun HorizontalSpacerBrush(
    modifier: Modifier = Modifier,
    height: Dp = paddingMedium,
    backgroundBrush: Brush = Brush.horizontalGradient(
        listOf(
            //primaryBlack,
            secondaryGreen,
            MaterialTheme.colorScheme.primaryContainer
        )
    )
) {
    Spacer(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(backgroundBrush)
            .innerShadow(
                shape = RoundedCornerShape(0),
                shadow = Shadow(
                    radius = 3.dp,
                    spread = 1.dp,
                    color = primaryBlack.copy(alpha = 0.75f),
                    offset = DpOffset(x = 0.dp, 4.dp)
                )
            )
    )
}