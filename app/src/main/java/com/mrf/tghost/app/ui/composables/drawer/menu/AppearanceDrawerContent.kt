package com.mrf.tghost.app.ui.composables.drawer.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingBig
import com.mrf.tghost.domain.model.ThemeItem

@Composable
fun AppearanceDrawerContent(
    currentTheme: String,
    isProUser: Boolean,
    onApplyCtaClick: (ThemeItem) -> Unit
) {

    Column(
        verticalArrangement = Arrangement.spacedBy(paddingBig),
    ) {

        ThemeSelectorLayout(
            currentTheme = currentTheme,
            isProUser = isProUser,
            onApplyCtaClick = {
                onApplyCtaClick(it)
            }
        )
    }


}