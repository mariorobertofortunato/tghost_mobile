package com.mrf.tghost.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp
import com.mrf.tghost.app.ui.theme.CornerSizes.extraLarge
import com.mrf.tghost.app.ui.theme.CornerSizes.extraSmall
import com.mrf.tghost.app.ui.theme.CornerSizes.large
import com.mrf.tghost.app.ui.theme.CornerSizes.medium
import com.mrf.tghost.app.ui.theme.CornerSizes.small

val shapes = Shapes(
    extraSmall = RoundedCornerShape(extraSmall),
    small = RoundedCornerShape(small),
    medium = RoundedCornerShape(medium),
    large = RoundedCornerShape(large),
    extraLarge = RoundedCornerShape(extraLarge)
)

object CornerSizes {
    val extraSmall = 4.dp
    val small = 8.dp
    val medium = 16.dp
    val large = 24.dp
    val extraLarge = 32.dp
}