package com.mrf.tghost.app.ui.composables.drawer.budget.budgetAdjust

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.mrf.tghost.R
import com.mrf.tghost.app.ui.composables.cta.BaseCta
import com.mrf.tghost.app.ui.composables.cta.TextLinkCta
import com.mrf.tghost.app.ui.composables.text.TextComponent
import com.mrf.tghost.app.ui.theme.BorderDimensions.borderNormal
import com.mrf.tghost.app.ui.theme.IconDimensions.iconNormal
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingNormal
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingSmall
import com.mrf.tghost.app.utils.enums.TextSize

@Composable
fun BudgetAdjustCtaContainer (
    category: String,
    isMinusEnabled: Boolean,
    isPlusEnabled: Boolean,
    onMinusClick: ()-> Unit,
    onPlusClick: ()-> Unit,
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = borderNormal,
                color = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            )
            .padding(paddingNormal)
    ) {

        if(isMinusEnabled){
            BaseCta(
                icon = painterResource(R.drawable.ic_minus),
                onClick = onMinusClick,
                modifier = Modifier
                    .padding(paddingSmall)
            )
        } else {
            TextLinkCta(
                icon = painterResource(R.drawable.ic_minus),
                isEnabled = false,
                onClick = {  },
                modifier = Modifier
                    .padding(paddingSmall),
            )
        }

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            TextComponent(
                text = stringResource(R.string.adjust),
                textSize = TextSize.CALLOUT,
                textAlign = TextAlign.Center,
            )
            TextComponent(
                text = category.uppercase(),
                textSize = TextSize.BODY,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        }

        if (isPlusEnabled) {
            BaseCta(
                icon = painterResource(R.drawable.ic_plus),
                onClick = onPlusClick,
                modifier = Modifier
                    .padding(paddingSmall)
            )
        } else {
            TextLinkCta(
                icon = painterResource(R.drawable.ic_plus),
                isEnabled = false,
                modifier = Modifier
                    .padding(paddingSmall),
                onClick = {  }
            )
        }



    }

}

@Composable
@Preview
private fun BudgetAdjustCtaContainerPreview() {
    BudgetAdjustCtaContainer(
        category = "Needs", isPlusEnabled = false, isMinusEnabled = true,
        onPlusClick = {  },
        onMinusClick = {  }
    )
}