package com.mrf.tghost.app.ui.screens.mainscreen.sections.portfolio

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mrf.tghost.app.contracts.MainScreenState
import com.mrf.tghost.app.ui.composables.decoration.HorizontalSpacer
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingSmall

@Composable
fun PortfolioCard(
    state: MainScreenState
){
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = MaterialTheme.shapes.large,
        tonalElevation = 2.dp,
        modifier = Modifier
            .animateContentSize()
            .padding(horizontal = paddingSmall)
    ) {
        Column {
            PortfolioHeader()
            HorizontalSpacer(height = 7.dp)
            PortfolioLayout(state)
        }

    }
}