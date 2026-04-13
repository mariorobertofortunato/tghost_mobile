package com.mrf.tghost.app.ui.screens.walletdetailsscreen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.mrf.tghost.app.contracts.WalletState
import com.mrf.tghost.app.ui.composables.ViewState
import com.mrf.tghost.app.ui.composables.text.AnimatedGradientText
import com.mrf.tghost.app.ui.screens.mainscreen.sections.tokenaccount.TokenAccounts
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingExtraSmall
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingNormal
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingSmall

@Composable
fun WalletDetailsScreenContent(
    wallet: WalletState?
) {

    val colorScheme = MaterialTheme.colorScheme

    Column(
        verticalArrangement = Arrangement.spacedBy(paddingNormal),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddingSmall)
    ) {

        WalletDetailsInfo(wallet)

        AnimatedContent(
            targetState = wallet?.walletViewState,
            transitionSpec = {
                slideInVertically { it } + fadeIn() togetherWith
                        slideOutVertically { -it } + fadeOut()
            },
            label = "loadingStage"
        ) { viewState ->
            when (viewState) {
                is ViewState.Loading -> {
                    AnimatedGradientText(text = wallet?.loadingStage ?: "// loading")
                }

                is ViewState.Success -> {
                    Column() {

                        WalletDetailsNetWorth(wallet)

                        AnimatedContent(
                            targetState = wallet?.loadingStage,
                            transitionSpec = {
                                slideInVertically { it } + fadeIn() togetherWith
                                        slideOutVertically { -it } + fadeOut()
                            },
                            label = "loadingStage"
                        ) {
                            if (it != null) {
                                Column(verticalArrangement = Arrangement.spacedBy(paddingExtraSmall)) {
                                    AnimatedGradientText(
                                        text = it,
                                        modifier = Modifier.padding(top = paddingSmall)
                                    )
                                    LinearProgressIndicator(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f, false),
                                        color = MaterialTheme.colorScheme.primary,
                                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                        strokeCap = StrokeCap.Round
                                    )
                                }
                            }
                        }
                    }

                }

                else -> Box(Modifier) /* Idle: nessuno spazio */
            }
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth(),
            color = colorScheme.surfaceContainerLow,
            shape = MaterialTheme.shapes.large,
            tonalElevation = 2.dp
        ) {
            TokenAccounts(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                walletState = wallet ?: return@Surface,
                expanded = true
            )
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}
