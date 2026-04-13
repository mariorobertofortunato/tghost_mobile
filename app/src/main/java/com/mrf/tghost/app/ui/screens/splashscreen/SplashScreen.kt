package com.mrf.tghost.app.ui.screens.splashscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.mrf.tghost.R
import com.mrf.tghost.app.compositions.NavigationEventTunnel
import com.mrf.tghost.app.contracts.NavigationGraphEvent
import com.mrf.tghost.app.navigation.Routes
import com.mrf.tghost.app.ui.composables.dialog.PopupWrapper
import com.mrf.tghost.app.ui.composables.dialog.WarningPopupContent
import com.mrf.tghost.app.viewmodel.OnboardingViewModel
import com.mrf.tghost.domain.model.Result
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onboardingViewModel: OnboardingViewModel = hiltViewModel()
) {
    val navigationEventTunnel = NavigationEventTunnel.current
    val onboardingCompletedFlag by onboardingViewModel.onboardingCompletedFlag.collectAsState()

    LaunchedEffect(Unit) {
        onboardingViewModel.getOnboardingStatus()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.axii_app_icon_1024),
                contentDescription = stringResource(R.string.splash_screen_image),
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
            )
        }
    }
    
    HandleSplashScreenNavigation(
        onboardingCompletedFlag = onboardingCompletedFlag,
        navigationEventTunnel = navigationEventTunnel
    )
}

@Composable
private fun HandleSplashScreenNavigation(
    onboardingCompletedFlag: Result<Boolean>,
    navigationEventTunnel: (NavigationGraphEvent) -> Unit
) {
    when (onboardingCompletedFlag) {
        is Result.Loading -> {}
        is Result.Success -> {
            LaunchedEffect(onboardingCompletedFlag) {
                navigationEventTunnel(
                    NavigationGraphEvent.NavigateTo(
                        destination = Routes.MainScreen,
                        popUpTo = Routes.SplashScreen,
                        inclusive = true
                    )
                )
            }
        }
        is Result.Failure -> {
            if (onboardingCompletedFlag.errorMessage == "Incomplete") {
                LaunchedEffect(onboardingCompletedFlag) {
                    navigationEventTunnel(
                        NavigationGraphEvent.NavigateTo(
                            destination = Routes.OnBoardingScreen,
                            popUpTo = Routes.SplashScreen,
                            inclusive = true
                        )
                    )
                }
            } else {
                PopupWrapper(
                    content = {
                        WarningPopupContent(
                            title = stringResource(R.string.error_popup_title),
                            body = onboardingCompletedFlag.errorMessage ?: "An error occurred",
                            onConfirm = {}
                        )
                    },
                    onDismiss = {}
                )
            }
        }
    }
}
