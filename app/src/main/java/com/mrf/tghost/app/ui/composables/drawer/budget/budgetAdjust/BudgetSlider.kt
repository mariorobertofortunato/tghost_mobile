package com.mrf.tghost.app.ui.composables.drawer.budget.budgetAdjust

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mrf.tghost.app.ui.composables.text.TextComponent
import com.mrf.tghost.app.ui.theme.BorderDimensions.borderSmall
import com.mrf.tghost.app.ui.theme.DividerDimensions.dividerBig
import com.mrf.tghost.app.ui.theme.HeightDimensions.heightNormal
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingExtraSmall
import com.mrf.tghost.app.utils.enums.TextSize

@Composable
fun BudgetSlider(
    sliderValue: Int,
    modifier: Modifier,
    color: Color = MaterialTheme.colorScheme.onPrimary
) {

    BoxWithConstraints(
        contentAlignment = Alignment.CenterStart,
        modifier = modifier
            .fillMaxWidth()
    ) {

        val sliderTotalWidthDp = maxWidth
        val sliderIndicatorWidth = (sliderTotalWidthDp * sliderValue / 100).coerceAtLeast(48.dp)

        // TRACK
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(dividerBig)
                .background(
                    color = Color.Transparent,
                    shape = CircleShape
                )
                .border(
                    width = borderSmall,
                    color = color,
                    shape = CircleShape
                )
        )

        // INDICATOR
        Box(
            modifier = Modifier
                .background(
                    color = color,
                    shape = CircleShape
                )

        ) {
            Spacer(
                modifier = Modifier
                    .height(heightNormal)
                    .width(sliderIndicatorWidth)
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .align(Alignment.Center)
            ) {
                TextComponent(
                    text = "${sliderValue}%",
                    color = MaterialTheme.colorScheme.primary,
                    textSize = TextSize.BODY,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(paddingExtraSmall)
                )
            }
        }
    }
}

@Composable
@Preview
private fun BudgetSliderPreview() {
    BudgetSlider(
        sliderValue = 50,
        modifier = Modifier
    )
}