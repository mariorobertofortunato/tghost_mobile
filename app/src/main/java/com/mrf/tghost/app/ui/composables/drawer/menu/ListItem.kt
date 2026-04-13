package com.mrf.tghost.app.ui.composables.drawer.menu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.mrf.tghost.app.ui.composables.decoration.HorizontalDivider
import com.mrf.tghost.app.ui.composables.text.TextComponent
import com.mrf.tghost.app.ui.theme.DividerDimensions.dividerExtraSmall
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingNormal
import com.mrf.tghost.app.utils.enums.LetterSpacing
import com.mrf.tghost.app.utils.enums.TextSize

@Composable
fun ListItem (
    value: String,
    onClick: () -> Unit
){

    Column(
        verticalArrangement = Arrangement.spacedBy(paddingNormal),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = null,
                indication = null,
                onClick = onClick
            )
    ){
        TextComponent(
            text = value,
            textSize = TextSize.BODY,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = LetterSpacing.FIVE,
            modifier = Modifier
                .padding(top = paddingNormal)
        )
        HorizontalDivider(
            thickness = dividerExtraSmall,
        )
    }
}