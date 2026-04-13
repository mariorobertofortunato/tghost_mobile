package com.mrf.tghost.app.ui.screens.mainscreen.sections.tokenaccount

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mrf.tghost.app.contracts.WalletState
import com.mrf.tghost.app.ui.composables.decoration.HorizontalSpacer
import com.mrf.tghost.domain.model.TokenAccountCategories

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TokenAccounts(
    modifier: Modifier = Modifier, // We need this in order to have the chance to pass a vertical scrollable modifier for the WalletDetailScreen
    walletState: WalletState,
    expanded: Boolean = false
) {
    val pageSize = 5
    var selectedDestination by rememberSaveable { mutableStateOf(Destination.HOLDINGS) }
    var visibleItemCount by rememberSaveable(selectedDestination) { mutableIntStateOf(pageSize) }
    var visibleTxCount by rememberSaveable(selectedDestination) { mutableIntStateOf(pageSize) }

    LaunchedEffect(expanded) {
        if (!expanded) {
            visibleItemCount = pageSize
            visibleTxCount = pageSize
        }
    }

    val fullTokenList by remember(walletState.tokenAccounts, selectedDestination) {
        derivedStateOf {
            val category = when (selectedDestination) {
                Destination.HOLDINGS -> TokenAccountCategories.HOLDINGS
                Destination.DEFI -> TokenAccountCategories.DEFI
                Destination.NFTS -> TokenAccountCategories.NFTS
                else -> {}
            }
            walletState.tokenAccounts
                .filter { it.tokenAccountCategory == category }
                .sortedByDescending { it.valueUsd }
        }
    }

    val currentTokenList by remember(fullTokenList, visibleItemCount) {
        derivedStateOf {
            fullTokenList.take(visibleItemCount)
        }
    }

    val fullTxList = walletState.transactions

    val currentTxList by remember(fullTxList, visibleTxCount) {
        derivedStateOf {
            fullTxList.take(visibleTxCount)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {

        SecondaryTabRow(
            selectedTabIndex = selectedDestination.ordinal,
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            divider = {},
            indicator = {
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(selectedDestination.ordinal),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        ) {
            Destination.entries.forEach { destination ->
                Tab(
                    selected = selectedDestination == destination,
                    onClick = { selectedDestination = destination },
                    text = {
                        Text(
                            text = destination.name,
                            style = if (selectedDestination == destination) MaterialTheme.typography.labelLarge else MaterialTheme.typography.labelSmall,
                            fontWeight = if (selectedDestination == destination) FontWeight.Bold else FontWeight.Medium,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        HorizontalSpacer(height = 1.dp)

        if (selectedDestination == Destination.TX) {

            TransactionTab(
                modifier = modifier,
                selectedDestination = selectedDestination,
                fullTxList = fullTxList,
                currentTxList = currentTxList,
                onLoadMoreClick = {
                    visibleTxCount += pageSize
                }
            )

        } else {

            TokenListsTab(
                modifier = modifier,
                selectedDestination = selectedDestination,
                fullTokenList = fullTokenList,
                currentTokenList = currentTokenList,
                onLoadMoreClick = {
                    visibleItemCount += pageSize
                }
            )

        }

    }
}

enum class Destination {
    HOLDINGS, DEFI, NFTS, TX
}
