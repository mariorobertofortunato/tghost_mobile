package com.mrf.tghost.app.ui.composables.text

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import com.mrf.tghost.app.ui.composables.lerp
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingSmall

@Composable
fun AnimatedGradientText(
    modifier: Modifier = Modifier,
    text: String,
    fontWeight: FontWeight? = FontWeight.Normal,
    letterSpacing: TextUnit? = TextUnit.Unspecified,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    gradientColorOne: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
    gradientColorTwo: Color = MaterialTheme.colorScheme.primary
){
    val infiniteTransition = rememberInfiniteTransition(label = "textGradient")
    val animatedProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "textGradientProgress"
    )

    val currentGradientColors = remember(animatedProgress) {
        listOf(
            lerp(gradientColorOne, gradientColorTwo, animatedProgress),
            lerp(gradientColorTwo, gradientColorOne, animatedProgress)
        )
    }

    Text(
        text = text,
        style = style.copy(
            brush = Brush.linearGradient(currentGradientColors)
        ),
        fontWeight = fontWeight,
        letterSpacing = letterSpacing ?: TextUnit.Unspecified,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = paddingSmall)
    )
}