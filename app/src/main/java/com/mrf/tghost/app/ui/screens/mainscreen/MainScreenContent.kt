package com.mrf.tghost.app.ui.screens.mainscreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mrf.tghost.app.contracts.MainScreenState
import com.mrf.tghost.app.ui.screens.mainscreen.sections.portfolio.PortfolioCard
import com.mrf.tghost.app.ui.screens.mainscreen.sections.wallets.WalletsCard
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingMedium
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingNormal

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreenContent(
    modifier: Modifier = Modifier,
    state: MainScreenState
) {

    Column(
        verticalArrangement = Arrangement.spacedBy(paddingMedium),
        modifier = modifier
            .fillMaxSize()
            .padding(top = paddingNormal)
    ) {

        PortfolioCard(state)
        WalletsCard(state)
    }
}
