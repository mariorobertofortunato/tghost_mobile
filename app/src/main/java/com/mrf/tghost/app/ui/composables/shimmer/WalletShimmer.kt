package com.mrf.tghost.app.ui.composables.shimmer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.mrf.tghost.R
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingExtraSmall
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingSmall
import com.mrf.tghost.app.utils.extensions.shimmerEffect

@Composable
fun WalletShimmer() {
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
    )
    val shimmerShape = RoundedCornerShape(paddingSmall)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(paddingSmall),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = paddingSmall)
    ) {

        WalletShimmerChainIcon(shimmerColors, shimmerShape)

        Column(
            verticalArrangement = Arrangement.spacedBy(paddingExtraSmall),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "LOADING",
                color = Color.Transparent,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.shimmerEffect(colors = shimmerColors, shape = shimmerShape)
            )
            Text(
                text = "LOADING",
                color = Color.Transparent,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.shimmerEffect(colors = shimmerColors, shape = shimmerShape)
            )

        }

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(paddingExtraSmall),
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                text = "LOADING",
                color = Color.Transparent,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.shimmerEffect(colors = shimmerColors, shape = shimmerShape)
            )
            Text(
                text = "LOADING",
                color = Color.Transparent,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.shimmerEffect(colors = shimmerColors, shape = shimmerShape)
            )
        }

    }
}

@Composable
fun WalletShimmerChainIcon(
    shimmerColors: List<Color>,
    shimmerShape: Shape = RoundedCornerShape(paddingSmall)
) {
    Box(
        modifier = Modifier
            .wrapContentSize()
            .shimmerEffect(colors = shimmerColors, shape = shimmerShape)
    ) {
        IconButton(
            onClick = {}
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_help),
                contentDescription = "Shimmer icon",
                tint = Color.Transparent,
                modifier = Modifier.padding(paddingExtraSmall)
            )
        }
    }
}


@Composable
@Preview
fun WalletShimmerPreview() {
    WalletShimmer()
}