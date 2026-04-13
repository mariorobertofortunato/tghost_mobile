package com.mrf.tghost.app.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Routes {
    @Serializable
    object MainScreen : Routes()

    @Serializable
    object SplashScreen : Routes()

    @Serializable
    object OnBoardingScreen : Routes()

    @Serializable
    data class WalletDetailsScreen(
        val walletId: String? = null,
    )  : Routes()

    @Serializable
    object MenuScreen : Routes()

    @Serializable
    object NetworkScreen : Routes()

}