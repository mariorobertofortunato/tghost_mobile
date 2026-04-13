package com.mrf.tghost.app.ui.screens.mainscreen.sections.wallets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.innerShadow
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.mrf.tghost.R
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingExtraSmall
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingSmall
import com.mrf.tghost.app.ui.theme.primaryBlack

@Composable
fun WalletInfoChainIcon(
    iconRes: Int?,
    onClick: () -> Unit = {}
) {

    Box(
        modifier = Modifier
            .wrapContentSize()
            .background(
                MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(paddingSmall)
            )
            .innerShadow(
                shape = RoundedCornerShape(paddingSmall),
                shadow = Shadow(
                    radius = 3.dp,
                    spread = 1.dp,
                    color = primaryBlack.copy(alpha = 0.75f),
                    offset = DpOffset(x = 0.dp, 4.dp)
                )
            )
    ) {
        IconButton(
            onClick = {
                onClick()
            },
            modifier = Modifier
        ) {
            Icon(
                painter = painterResource(iconRes ?: R.drawable.ic_help),
                contentDescription = "chain icon",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(paddingExtraSmall)
            )
        }
    }
}