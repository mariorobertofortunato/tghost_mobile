package com.mrf.tghost.app.ui.composables.shimmer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingSmall
import com.mrf.tghost.app.utils.extensions.shimmerEffect

@Deprecated("Not really needed since we only show the '//Calculating portfolio' label")
@Composable
fun PortfolioShimmer(){
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
    )
    val shimmerShape = RoundedCornerShape(paddingSmall)
    val shimmerModifier = Modifier
        .heightIn(min = 28.dp)
        .shimmerEffect(colors = shimmerColors, shape = shimmerShape)

    Column(
        verticalArrangement = Arrangement.spacedBy(paddingSmall),
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddingSmall)
    ){
        Row(
            horizontalArrangement = Arrangement.spacedBy(paddingSmall),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ){
            Text(
                text = "LOADING",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = Color.Transparent,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .weight(1f)
                    .then(shimmerModifier)
            )
            Text(
                text = "LOADING",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = Color.Transparent,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .weight(1f)
                    .then(shimmerModifier)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(paddingSmall),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ){
            Text(
                text = "LOADING",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = Color.Transparent,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .weight(1f)
                    .then(shimmerModifier)
            )
            Text(
                text = "LOADING",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = Color.Transparent,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .weight(1f)
                    .then(shimmerModifier)
            )
        }

        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ){
            Text(
                text = "LOADING",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = Color.Transparent,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .then(shimmerModifier)
            )
        }


    }
}

@Preview
@Composable
fun PortfolioShimmerPreview (){
    PortfolioShimmer()
}