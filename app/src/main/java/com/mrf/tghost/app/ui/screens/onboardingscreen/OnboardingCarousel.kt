package com.mrf.tghost.app.ui.screens.onboardingscreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.mrf.tghost.R
import com.mrf.tghost.app.ui.composables.cta.TextLinkCta
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingNormal
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingSmall
import com.mrf.tghost.app.ui.theme.TGhostTheme
import com.mrf.tghost.domain.model.OnBoardingScreenItem

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingCarousel(
    items: ArrayList<OnBoardingScreenItem>,
    onComplete: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { items.size })

    val isLastStep by remember {
        derivedStateOf {
            pagerState.currentPage == items.lastIndex
        }
    }

    val isFirstStep by remember {
        derivedStateOf {
            pagerState.currentPage == 0
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    // Usiamo heightIn per mantenere l'altezza fissa della barra
                    // anche quando il pulsante Skip scompare
                    .heightIn(min = 64.dp)
                    .padding(horizontal = paddingNormal, vertical = paddingSmall)
            ) {
                Text(
                    text = stringResource(id = R.string.welcome),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterStart)
                )

                if (!isLastStep) {
                    TextLinkCta(
                        text = stringResource(id = R.string.skip),
                        onClick = onComplete,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }
            }
        },
        bottomBar = {
            OnboardingBottom(
                isFirstStep = isFirstStep,
                isLastStep = isLastStep,
                pagerState = pagerState,
                itemsSize = items.size,
                onComplete = onComplete,
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(paddingNormal),
            )
        }
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) { page ->
            OnboardingContent(
                item = items[page],
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun OnboardingCarouselPreview() {
    TGhostTheme {
        OnboardingCarousel(
            items = arrayListOf(
                OnBoardingScreenItem(
                    id = 1,
                    imageResId = R.drawable.onboarding_2,
                    title = stringResource(R.string.on_boarding_screen_title1),
                    desc = stringResource(R.string.on_boarding_screen_desc1)
                ),
                OnBoardingScreenItem(
                    id = 2,
                    imageResId = R.drawable.onboarding_2,
                    title = stringResource(R.string.on_boarding_screen_title2),
                    desc = stringResource(R.string.on_boarding_screen_desc2)
                ),
                OnBoardingScreenItem(
                    id = 3,
                    imageResId = R.drawable.onboarding_2,
                    title = stringResource(R.string.on_boarding_screen_title3),
                    desc = stringResource(R.string.on_boarding_screen_desc3)
                )
            ),
            onComplete = { Unit }
        )
    }
}
