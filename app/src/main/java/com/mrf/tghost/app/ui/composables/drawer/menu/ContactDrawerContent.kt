package com.mrf.tghost.app.ui.composables.drawer.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.mrf.tghost.R
import com.mrf.tghost.app.ui.composables.cta.BaseCtaFullWidth
import com.mrf.tghost.app.ui.composables.text.TextComponent
import com.mrf.tghost.app.ui.theme.IconDimensions.iconSmall
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingBig
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingNormal
import com.mrf.tghost.app.utils.enums.TextSize

@Composable
fun ContactDrawerContent (
    onMailCtaClick: () -> Unit
){

    val scrollState = rememberScrollState()

    Column(
        verticalArrangement = Arrangement.spacedBy(paddingBig),
    ){
        Column(
            verticalArrangement = Arrangement.spacedBy(paddingNormal),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .verticalScroll(scrollState)
                .weight(1f, false)
        ){
            TextComponent(
                text = stringResource(R.string.contact_drawer_body),
                textSize = TextSize.BODY
            )
        }
        BaseCtaFullWidth(
            text = stringResource(R.string.email),
            textSize = TextSize.BODY,
            icon = painterResource(R.drawable.ic_mail),
            iconSize = iconSmall,
            onClick = onMailCtaClick
        )
    }
}