package com.mrf.tghost.app.ui.composables.drawer.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.mrf.tghost.R
import com.mrf.tghost.app.ui.composables.ListItem
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingSmall

@Composable
fun PrivacyAndTermsDrawerContent (
    onPrivacyClick: () -> Unit,
    onTermsOfServiceClick: () -> Unit,
    onDisclaimerClick: () -> Unit
){

    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(paddingSmall)
    ){}
    ListItem(
        label = stringResource(R.string.menu_item_privacy_policy),
        onClick = {
            onPrivacyClick()
        },
        icon = painterResource(R.drawable.ic_privacy)
    )
    ListItem(
        label = stringResource(R.string.menu_item_terms_of_service),
        onClick = {
            onTermsOfServiceClick()
        },
        icon = painterResource(R.drawable.ic_terms_of_service_book)
    )
    ListItem(
        label = stringResource(R.string.menu_item_disclaimer),
        onClick = {
            onDisclaimerClick()
        },
        icon = painterResource(R.drawable.ic_disclaimer)
    )
}