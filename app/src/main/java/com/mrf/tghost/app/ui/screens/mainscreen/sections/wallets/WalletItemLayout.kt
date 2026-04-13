package com.mrf.tghost.app.ui.screens.mainscreen.sections.wallets

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mrf.tghost.app.contracts.WalletState
import com.mrf.tghost.app.ui.composables.ViewState
import com.mrf.tghost.app.ui.composables.shimmer.WalletShimmer
import com.mrf.tghost.app.ui.composables.text.AnimatedGradientText
import com.mrf.tghost.app.ui.screens.mainscreen.sections.tokenaccount.TokenAccounts
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingNormal
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingSmall
import com.mrf.tghost.app.utils.TWEEN_DURATION

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletItemLayout(
    walletState: WalletState
) {

    Column(
        verticalArrangement = Arrangement.spacedBy(paddingSmall)
    ) {
        AnimatedContent(
            targetState = walletState.walletViewState,
            transitionSpec = {
                when (targetState) {
                    is ViewState.Success if initialState is ViewState.Loading -> {
                        (slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = tween(TWEEN_DURATION)
                        ) + fadeIn(tween(100))) togetherWith
                                (slideOutHorizontally(
                                    targetOffsetX = { -it },
                                    animationSpec = tween(TWEEN_DURATION)
                                ) + fadeOut(tween(100)))
                    }

                    is ViewState.Loading if initialState is ViewState.Success -> {
                        (slideInHorizontally(
                            initialOffsetX = { -it },
                            animationSpec = tween(TWEEN_DURATION)
                        ) + fadeIn(tween(100))) togetherWith
                                (slideOutHorizontally(
                                    targetOffsetX = { it },
                                    animationSpec = tween(TWEEN_DURATION)
                                ) + fadeOut(tween(100)))
                    }

                    else -> fadeIn(tween(100)) togetherWith fadeOut(tween(100))
                }
            },
            label = "walletViewState"
        ) { viewState ->
            when (viewState) {
                is ViewState.Loading -> WalletShimmer()
                is ViewState.Success -> Column(
                    verticalArrangement = Arrangement.spacedBy(paddingSmall),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AnimatedContent(
                        targetState = walletState.loadingStage,
                        transitionSpec = {
                            slideInVertically { it } + fadeIn() togetherWith
                                    slideOutVertically { -it } + fadeOut()
                        },
                        label = "loadingStage"
                    ) {
                        if (it != null) {
                            AnimatedGradientText(
                                text = it,
                                modifier = Modifier.padding(top = paddingSmall)
                            )
                        }
                    }
                    Wallet(walletState)
                }
                else -> Box(Modifier) /* Idle: nessuno spazio */
            }
        }

    }
}

@Composable
fun Wallet(
    walletState: WalletState
) {

    var expanded by remember { mutableStateOf(false) }

    ElevatedCard(
        elevation = CardDefaults.elevatedCardElevation(0.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        )
    ) {
        Column(
            modifier = Modifier
                .animateContentSize(animationSpec = tween(durationMillis = TWEEN_DURATION / 2))
        ) {
            WalletInfo(
                walletState = walletState,
                isExpanded = expanded,
                onItemClicked = { expanded = !expanded }
            )
            if (walletState.loadingStage != null) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    strokeCap = StrokeCap.Round
                )
            }
            AnimatedVisibility(
                visible = expanded,
                modifier = Modifier.fillMaxWidth(),
                enter = expandVertically(animationSpec = tween(durationMillis = TWEEN_DURATION / 2)) + fadeIn(
                    animationSpec = tween(durationMillis = TWEEN_DURATION / 2)
                ),
                exit = shrinkVertically(animationSpec = tween(durationMillis = TWEEN_DURATION / 2)) + fadeOut(
                    animationSpec = tween(durationMillis = TWEEN_DURATION / 2)
                )
            ) {
                Column {
                    walletState.errorList.forEach { message ->
                        ErrorBanner(message)
                    }
                    TokenAccounts(walletState = walletState, expanded = expanded)
                }
            }
        }
    }


}

@Composable
fun ErrorBanner(message: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f))
            .padding(paddingNormal),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(paddingSmall)
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onErrorContainer,
            textAlign = TextAlign.Start,
            modifier = Modifier.weight(1f)
        )
    }
}




