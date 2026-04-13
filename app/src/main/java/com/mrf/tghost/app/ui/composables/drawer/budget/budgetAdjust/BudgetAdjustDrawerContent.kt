package com.mrf.tghost.app.ui.composables.drawer.budget.budgetAdjust

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.mrf.tghost.R
import com.mrf.tghost.app.ui.composables.cta.BaseCtaFullWidth
import com.mrf.tghost.app.ui.composables.text.TextComponent
import com.mrf.tghost.app.ui.theme.IconDimensions.iconSmall
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingBig
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingNormal
import com.mrf.tghost.app.utils.enums.TextSize

@Composable
fun BudgetAdjustDrawerContent(
    needPerc: Int,
    wantPerc: Int,
    savePerc: Int,
    onBudgetValueChange: (category: String, sliderValue: Int, needsCategory: String, wantsCategory: String) -> Unit,
    onConfirm: () -> Unit
) {
    
    val needsCategory = stringResource(R.string.needs)
    val wantsCategory = stringResource(R.string.wants)
    val savingsCategory = stringResource(R.string.savings)
    
    val categories = listOf(needsCategory, wantsCategory, savingsCategory)
    
    var selectedCategory by remember { mutableStateOf(categories[0]) }

    Column(
        verticalArrangement = Arrangement.spacedBy(paddingBig),
    ){
        Column(
            verticalArrangement = Arrangement.spacedBy(paddingNormal),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        ) {

            TextComponent(
                text = stringResource(R.string.select_category_to_adjust),
                textSize = TextSize.BODY,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .fillMaxWidth()
            )

            BudgetSliderContainer(
                category = needsCategory,
                sliderValue = needPerc,
                isSelected = selectedCategory == needsCategory,
                onClick = { selectedCategory = needsCategory }
            )
            BudgetSliderContainer(
                category = wantsCategory,
                sliderValue = wantPerc,
                isSelected = selectedCategory == wantsCategory,
                onClick = { selectedCategory = wantsCategory }
            )

            BudgetSliderContainer(
                category = savingsCategory,
                sliderValue = savePerc,
                isSelected = selectedCategory == savingsCategory,
                color = MaterialTheme.colorScheme.primaryContainer,
                onClick = {
                    // Do nothing
                }
            )


            BudgetAdjustCtaContainer(
                category = selectedCategory,
                isPlusEnabled = canIncrement(selectedCategory, needPerc, wantPerc, needsCategory, wantsCategory),
                isMinusEnabled = canDecrement(selectedCategory, needPerc, wantPerc, needsCategory, wantsCategory),
                onPlusClick = {
                    onBudgetValueChange(selectedCategory, +10, needsCategory, wantsCategory)
                },
                onMinusClick = {
                    onBudgetValueChange(selectedCategory, -10, needsCategory, wantsCategory)
                }
            )

        }
        BaseCtaFullWidth(
            text = stringResource(R.string.apply),
            textSize = TextSize.BODY,
            icon = painterResource(R.drawable.ic_confirm),
            iconSize = iconSmall,
            onClick = onConfirm,
            modifier = Modifier
        )
    }


}


// Helper Functions
private fun canIncrement(selectedCategory: String, needPerc: Int, wantPerc: Int, needsCategory: String, wantsCategory: String): Boolean {
    return when (selectedCategory) {
        needsCategory -> needPerc < 100 && ((needPerc + wantPerc) < 100)
        wantsCategory -> wantPerc < 100 && ((needPerc + wantPerc) < 100)
        else -> false
    }
}

private fun canDecrement(selectedCategory: String, needPerc: Int, wantPerc: Int, needsCategory: String, wantsCategory: String): Boolean {
    return when (selectedCategory) {
        needsCategory -> needPerc > 0
        wantsCategory -> wantPerc > 0
        else -> false
    }
}



@Composable
@Preview
private fun BudgetAdjustPopupPreview() {
    BudgetAdjustDrawerContent(
        onBudgetValueChange = { _, _, _, _ -> Unit },
        //onDismiss = { Unit },
        onConfirm = { Unit },
        needPerc = 50,
        wantPerc = 30,
        savePerc = 20
    )
}