package com.mrf.tghost.app.ui.screens.mainscreen.sections.portfolio

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mrf.tghost.app.contracts.MainScreenState
import com.mrf.tghost.app.ui.composables.text.AnimatedGradientText
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingSmall
import com.mrf.tghost.app.utils.TWEEN_DURATION
import com.mrf.tghost.app.utils.extensions.smartFormatAmount
import com.mrf.tghost.domain.model.SupportedChain

@Composable
fun PortfolioLayout(state: MainScreenState) {
    AnimatedContent(
        targetState = state.isLoading,
        transitionSpec = {
            if (targetState && !initialState) {
                slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(TWEEN_DURATION)) togetherWith
                        slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(TWEEN_DURATION))
            } else {
                slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(TWEEN_DURATION)) togetherWith
                        slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(TWEEN_DURATION))
            }
        },
        label = "portfolioLoading"
    ) { portfolioIsLoading ->
        if (portfolioIsLoading) {
            AnimatedGradientText(
                text = "// Syncing assets ...",
                modifier = Modifier.padding(paddingSmall)
            )
        } else {
            if (state.wallets.isNotEmpty()) {
                Portfolio(state)
            } else {
                Text(
                    text = "// No wallets found",
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    modifier = Modifier.padding(paddingSmall)
                )
            }

        }
    }
}

@Composable
fun Portfolio(state: MainScreenState) {
    Column(
        verticalArrangement = Arrangement.spacedBy(paddingSmall),
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddingSmall)
    ) {
        // Main Crypto Assets Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(paddingSmall)
        ) {
            CryptoBalanceItem(
                visible = state.portfolioSolNativeBalance > 0,
                balance = state.portfolioSolNativeBalance,
                symbol = SupportedChain.SOLANA.chain.symbol ?: "",
                modifier = Modifier.weight(1f)
            )

            CryptoBalanceItem(
                visible = state.portfolioEvmNativeBalance > 0,
                balance = state.portfolioEvmNativeBalance,
                symbol = SupportedChain.EVM.chain.symbol ?: "",
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(paddingSmall)
        ) {
            CryptoBalanceItem(
                visible = state.portfolioSuiNativeBalance > 0,
                balance = state.portfolioSuiNativeBalance,
                symbol = SupportedChain.SUI.chain.symbol ?: "",
                modifier = Modifier.weight(1f)
            )

            CryptoBalanceItem(
                visible = state.portfolioTezNativeBalance > 0,
                balance = state.portfolioTezNativeBalance,
                symbol = SupportedChain.TEZ.chain.symbol ?: "",
                modifier = Modifier.weight(1f)
            )
        }

        // Total USD Balance - Highlighting the total
        AnimatedVisibility(
            visible = state.portfolioUSdBalance > 0,
            modifier = Modifier.fillMaxWidth()
        ) {
            PortfolioItem(
                chainSymbol = "USD",
                value = state.portfolioUSdBalance.smartFormatAmount(2, 2),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun CryptoBalanceItem(
    visible: Boolean,
    balance: Double,
    symbol: String,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier
    ) {
        AnimatedContent(
            targetState = balance,
            transitionSpec = {
                slideInVertically { it / 2 } + fadeIn() togetherWith
                        fadeOut(animationSpec = tween(TWEEN_DURATION))
            },
            label = "cryptoBalance"
        ) { currentBalance ->
            PortfolioItem(
                chainSymbol = symbol,
                value = currentBalance.smartFormatAmount(2, 9),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }
    }
}
