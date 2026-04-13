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
import com.mrf.tghost.app.ui.screens.mainscreen.sections.transactions.TransactionItem
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingSmall
import com.mrf.tghost.domain.model.Transaction

@Composable
fun TransactionTab(
    modifier: Modifier = Modifier,
    selectedDestination: Destination,
    fullTxList: List<Transaction>,
    currentTxList: List<Transaction>,
    onLoadMoreClick: () -> Unit
) {

    if (currentTxList.isNotEmpty()) {
        Column(modifier = modifier) {
            TxList(currentTxList)

            if (fullTxList.size > currentTxList.size) {
                TextButton(
                    onClick = onLoadMoreClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingSmall)
                ) {
                    Text(
                        text = "Load more (${fullTxList.size - currentTxList.size} remaining)",
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
fun TxList(transactions: List<Transaction>) {
    Column {
        transactions.forEachIndexed { index, tx ->
            key(tx.id) {
                TransactionItem(
                    modifier = Modifier
                        .padding(paddingSmall),
                    transaction = tx
                )
            }
        }
    }
}