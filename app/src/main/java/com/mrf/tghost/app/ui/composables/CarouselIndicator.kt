package com.mrf.tghost.app.ui.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingExtraSmall
import com.mrf.tghost.app.utils.TWEEN_DURATION

@Composable
fun CarouselIndicator(
    isSelected: Boolean
) {
    val width by animateDpAsState(
        targetValue = if (isSelected) 24.dp else 8.dp,
        animationSpec = tween(durationMillis = TWEEN_DURATION),
        label = "width"
    )

    val color by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.primaryContainer
        },
        animationSpec = tween(durationMillis = TWEEN_DURATION),
        label = "color"
    )

    Box(
        modifier = Modifier
            .padding(paddingExtraSmall)
            .height(8.dp)
            .width(width)
            .clip(CircleShape)
            .background(color)
    )
}
