package com.mrf.tghost.app.ui.screens.onboardingscreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.mrf.tghost.R
import com.mrf.tghost.app.ui.composables.CarouselIndicator
import com.mrf.tghost.app.ui.composables.cta.BaseCta
import com.mrf.tghost.app.ui.composables.cta.TextLinkCta
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingNormal
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingSmall
import com.mrf.tghost.app.ui.theme.TGhostTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingBottom(
    modifier: Modifier = Modifier,
    isFirstStep: Boolean,
    isLastStep: Boolean,
    pagerState: PagerState,
    itemsSize: Int,
    onComplete: () -> Unit
) {
    val animationScope = rememberCoroutineScope()

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(paddingNormal),
    ) {
        PagerIndicator(pagerState, itemsSize)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!isFirstStep) {
                TextLinkCta(
                    text = stringResource(R.string.prev),
                    onClick = {
                        animationScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    }
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            BaseCta(
                onClick = {
                    if (isLastStep) {
                        onComplete()
                    } else {
                        animationScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                text = stringResource(if (isLastStep) R.string.i_m_ready else R.string.next),
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PagerIndicator(pagerState: PagerState, pageCount: Int) {
    Row(
        modifier = Modifier.padding(vertical = paddingSmall),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            CarouselIndicator(isSelected = pagerState.currentPage == index)
        }
    }
}

@Composable
@PreviewLightDark
fun OnboardingBottomPreview() {
    TGhostTheme {
        OnboardingBottom(
            isFirstStep = false,
            isLastStep = false,
            pagerState = rememberPagerState(pageCount = { 3 }),
            itemsSize = 3,
            onComplete = {}
        )
    }
}
