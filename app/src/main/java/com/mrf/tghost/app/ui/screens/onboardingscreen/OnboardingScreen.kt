package com.mrf.tghost.app.ui.screens.onboardingscreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.mrf.tghost.R
import com.mrf.tghost.app.compositions.NavigationEventTunnel
import com.mrf.tghost.app.contracts.NavigationGraphEvent
import com.mrf.tghost.app.navigation.Routes
import com.mrf.tghost.app.ui.composables.ViewState
import com.mrf.tghost.app.ui.composables.loadingindicators.CircularLoadingIndicator
import com.mrf.tghost.app.viewmodel.OnboardingViewModel
import com.mrf.tghost.domain.model.OnBoardingScreenItem

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onboardingViewModel: OnboardingViewModel = hiltViewModel()
) {

    val navigationEventTunnel = NavigationEventTunnel.current
    val items = ArrayList<OnBoardingScreenItem>()
    val viewState by onboardingViewModel.viewState.collectAsState()

    items.addAll(
        listOf(
            OnBoardingScreenItem(
                id = 1,
                imageResId = R.drawable.onboarding_1,
                title = stringResource(R.string.on_boarding_screen_title1),
                desc = stringResource(R.string.on_boarding_screen_desc1)
            ),
            OnBoardingScreenItem(
                id = 2,
                imageResId = R.drawable.onboarding_2,
                title = stringResource(R.string.on_boarding_screen_title2),
                desc = stringResource(R.string.on_boarding_screen_desc2),
            ),
            OnBoardingScreenItem(
                id = 3,
                imageResId = R.drawable.onboarding_3,
                title = stringResource(R.string.on_boarding_screen_title3),
                desc = stringResource(R.string.on_boarding_screen_desc3)
            ),
        )
    )

    LaunchedEffect(viewState) {
        when (viewState) {
            is ViewState.Success -> {
                navigationEventTunnel(
                    NavigationGraphEvent.NavigateTo(
                        destination = Routes.MainScreen,
                        popUpTo = Routes.OnBoardingScreen,
                        inclusive = true
                    )
                )
            }
            else -> {}
        }
    }

    if (viewState is ViewState.Loading) {
        CircularLoadingIndicator()
        return
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
        ) {
            OnboardingCarousel(
                items = items,
                onComplete = { onboardingViewModel.completeOnboarding() }
            )
        }
    }
}
