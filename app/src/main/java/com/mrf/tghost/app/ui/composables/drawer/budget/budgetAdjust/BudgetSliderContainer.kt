package com.mrf.tghost.app.ui.composables.drawer.budget.budgetAdjust

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.mrf.tghost.app.ui.composables.text.TextComponent
import com.mrf.tghost.app.ui.theme.BorderDimensions.borderNormal
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingNormal
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingSmall
import com.mrf.tghost.app.ui.theme.shapes
import com.mrf.tghost.app.utils.enums.LetterSpacing
import com.mrf.tghost.app.utils.enums.TextSize

@Composable
fun BudgetSliderContainer(
    category: String,
    isSelected: Boolean? = false,
    sliderValue: Int,
    color: Color = MaterialTheme.colorScheme.onPrimary,
    onClick: () -> Unit
) {

    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(paddingSmall),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = borderNormal,
                color = if (isSelected == true) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                shape = shapes.medium
            )
            .padding(paddingNormal)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {

        TextComponent(
            text = category,
            textSize = TextSize.TITLE,
            fontWeight = FontWeight.Bold,
            letterSpacing = LetterSpacing.FIVE,
            color = color
        )

        BudgetSlider(
            sliderValue = sliderValue,
            color = color,
            modifier = Modifier
        )

    }

}





@Composable
@Preview
private fun BudgetSliderContainerPreview() {
    BudgetSliderContainer(
        category = "Needs",
        sliderValue = 50,
        isSelected = false,
        onClick = {  },
        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.25f),

        )
}