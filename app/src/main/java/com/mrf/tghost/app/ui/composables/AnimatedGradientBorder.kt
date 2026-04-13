package com.mrf.tghost.app.ui.composables

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedGradientBorder(
    modifier: Modifier = Modifier,
    borderWidth: Dp = 2.dp,
    initialColors: List<Color>,
    targetColors: List<Color>,
    animationDurationMillis: Int = 2,
    content: @Composable () -> Unit
) {
    require(initialColors.size >= 2 && targetColors.size == initialColors.size) {
        "InitialColors deve contenere almeno 2 colori e targetColors deve avere la stessa dimensione."
    }

    val infiniteTransition = rememberInfiniteTransition(label = "gradientBorderTransition")
    val animatedProgress = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = animationDurationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradientProgress"
    )

    val currentGradientColors = remember(animatedProgress.value) {
        initialColors.mapIndexed { index, initialColor ->
            lerp(initialColor, targetColors[index], animatedProgress.value)
        }
    }

    Box(
        modifier = modifier
            .drawWithContent {
                drawContent()
                drawRoundRect(
                    brush = Brush.linearGradient(
                        colors = currentGradientColors,
                        start = Offset.Zero,
                        end = Offset(
                            size.width,
                            size.height
                        )
                    ),
                    style = Stroke(width = borderWidth.toPx(), cap = StrokeCap.Round),
                    cornerRadius = CornerRadius(x = 24f, y = 24f)
                )
            }
    ) {
        content()
    }
}

fun lerp(start: Color, stop: Color, fraction: Float): Color {
    val r = lerp(start.red, stop.red, fraction)
    val g = lerp(start.green, stop.green, fraction)
    val b = lerp(start.blue, stop.blue, fraction)
    val a = lerp(start.alpha, stop.alpha, fraction)
    return Color(r, g, b, a)
}

fun lerp(start: Float, stop: Float, fraction: Float): Float {
    return (1 - fraction) * start + fraction * stop
}
