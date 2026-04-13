package com.mrf.tghost.app.ui.screens.mainscreen.sections.portfolio

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import com.mrf.tghost.R
import com.mrf.tghost.app.compositions.MainScreenEventTunnel
import com.mrf.tghost.app.contracts.MainScreenEvent
import com.mrf.tghost.app.ui.theme.IconDimensions.iconSmall
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingSmall

@Composable
fun PortfolioHeader() {
    val eventTunnel = MainScreenEventTunnel.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddingSmall)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = { eventTunnel(MainScreenEvent.FetchWallets) }
            )
    ) {
        Text(
            text = "portfolio".uppercase(),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            letterSpacing = TextUnit(0.2f, TextUnitType.Sp)
        )

        Icon(
            painter = painterResource(R.drawable.ic_overview_2_small),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(iconSmall)
        )
    }
}
