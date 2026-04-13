package com.mrf.tghost.app.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.mrf.tghost.app.compositions.NavigationEventTunnel
import com.mrf.tghost.app.contracts.NavigationGraphEvent
import com.mrf.tghost.app.ui.screens.mainscreen.MainScreen
import com.mrf.tghost.app.ui.screens.menuscreen.MenuScreen
import com.mrf.tghost.app.ui.screens.networkscreen.NetworkScreen
import com.mrf.tghost.app.ui.screens.onboardingscreen.OnboardingScreen
import com.mrf.tghost.app.ui.screens.splashscreen.SplashScreen
import com.mrf.tghost.app.ui.screens.walletdetailsscreen.WalletDetailsScreen
import com.mrf.tghost.app.viewmodel.NavigationViewModel
import com.mrf.tghost.app.utils.TWEEN_DURATION

@Composable
fun NavGraph(
    navigationViewModel: NavigationViewModel = hiltViewModel()
) {

    val navController = rememberNavController()
    val navState by navigationViewModel.state.collectAsState()

    CompositionLocalProvider(NavigationEventTunnel provides navState.eventTunnel) {

        DisposableEffect(navState.destination) {
            navState.destination?.let { route ->
                navController.navigate(route) {
                    navState.popUpTo?.let { popUpTo(it) { inclusive = navState.inclusive } }
                    launchSingleTop = true
                }
            }
            onDispose { navState.eventTunnel(NavigationGraphEvent.ClearNavigation) }
        }

        NavHost(
            navController = navController,
            startDestination = Routes.SplashScreen,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    tween(TWEEN_DURATION)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    tween(TWEEN_DURATION)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.End,
                    tween(TWEEN_DURATION)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.End,
                    tween(TWEEN_DURATION)
                )
            }
        ) {

            composable<Routes.SplashScreen> {
                SplashScreen()
            }
            composable<Routes.OnBoardingScreen> {
                OnboardingScreen()
            }
            composable<Routes.MainScreen> {
                MainScreen()
            }
            composable<Routes.WalletDetailsScreen> { backStackEntry ->
                val args: Routes.WalletDetailsScreen = backStackEntry.toRoute()
                WalletDetailsScreen(
                    walletId = args.walletId
                )
            }
            composable<Routes.MenuScreen> {
                MenuScreen()
            }
            composable<Routes.NetworkScreen> {
                NetworkScreen()
            }
        }


    }


}
