package com.mrf.tghost.app.ui.screens.mainscreen.sections.tokenaccount

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingSmall
import com.mrf.tghost.domain.model.TokenAccount

@Composable
fun TokenListsTab (
    modifier: Modifier = Modifier,
    selectedDestination: Destination,
    fullTokenList: List<TokenAccount>,
    currentTokenList: List<TokenAccount>,
    onLoadMoreClick: () -> Unit
){
    if (currentTokenList.isNotEmpty()) {

        Column(modifier = modifier){
            TokenList(tokenAccounts = currentTokenList, )

            if (fullTokenList.size > currentTokenList.size) {
                TextButton(
                    onClick = onLoadMoreClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingSmall)
                ) {
                    Text(
                        text = "Load more (${fullTokenList.size - currentTokenList.size} remaining)",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }

    } else {
        EmptyListPlaceholder(selectedDestination.name)
    }
}

@Composable
fun TokenList(tokenAccounts: List<TokenAccount>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
    ) {
        tokenAccounts.forEachIndexed { index, token ->
            key(token.pubkey ?: token.hashCode()) {
                TokenAccountItem(
                    modifier = Modifier
                        .padding(paddingSmall),
                    tokenAccount = token
                )
            }
        }
    }
}