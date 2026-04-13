package com.mrf.tghost.app.ui.screens.mainscreen.sections.wallets

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mrf.tghost.app.contracts.MainScreenState
import com.mrf.tghost.app.ui.composables.decoration.HorizontalSpacer
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingSmall
import java.util.UUID

@Composable
fun WalletsCard(
    state: MainScreenState
) {
    Surface(
        color = Color.Transparent,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        tonalElevation = 2.dp,
        modifier = Modifier
            .animateContentSize()
            .padding(horizontal = paddingSmall)
    ) {
        val listState = rememberLazyListState()

        Column {
            WalletsHeader()
            HorizontalSpacer(height = 7.dp)
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                MaterialTheme.colorScheme.surfaceContainerLow,
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    ),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(paddingSmall),
                contentPadding = PaddingValues(top = paddingSmall, bottom = 80.dp)
            ) {
                itemsIndexed(
                    items = state.wallets,
                    key = { _, it -> it.wallet?.publicKey ?: UUID.randomUUID() }
                ) { index, wallet ->
                    WalletItemLayout(walletState = wallet)
                }
            }
        }

    }
}