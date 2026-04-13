package com.mrf.tghost.app.ui.screens.mainscreen.sections.tokenaccount

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mrf.tghost.app.ui.theme.primaryGreen
import com.mrf.tghost.app.ui.theme.primaryRed
import com.mrf.tghost.domain.model.Liquidity
import com.mrf.tghost.domain.model.PriceChange
import com.mrf.tghost.domain.model.Token
import com.mrf.tghost.domain.model.TokenAccount
import com.mrf.tghost.domain.model.TokenMarketData
import com.mrf.tghost.domain.model.Transactions
import com.mrf.tghost.domain.model.Volume
import com.mrf.tghost.app.utils.extensions.maxDecimalPlacesString

@Composable
fun PriceChangeRow(tokenAccount: TokenAccount) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier
            .fillMaxWidth()
    ) {

        if (tokenAccount.priceChange?.h24 != null) {
            Text(
                text = "24h: ${tokenAccount.priceChange?.h24?.maxDecimalPlacesString(2)}%",
                style = MaterialTheme.typography.labelSmall,
                color = if ((tokenAccount.priceChange?.h24 ?: 0.0) > 0.0) primaryGreen else primaryRed,
            )
        }
        if (tokenAccount.priceChange?.h6 != null) {
            Text(
                text = "6h: ${tokenAccount.priceChange?.h6?.maxDecimalPlacesString(2)}%",
                style = MaterialTheme.typography.labelSmall,
                color = if ((tokenAccount.priceChange?.h6 ?: 0.0) > 0.0) primaryGreen else primaryRed,
            )
        }
        if (tokenAccount.priceChange?.h1 != null) {
            Text(
                text = "1h: ${tokenAccount.priceChange?.h1?.maxDecimalPlacesString(2)}%",
                style = MaterialTheme.typography.labelSmall,
                color = if ((tokenAccount.priceChange?.h1 ?: 0.0) > 0.0) primaryGreen else primaryRed,
            )
        }
        if (tokenAccount.priceChange?.m5 != null) {
            Text(
                text = "5m: ${tokenAccount.priceChange?.m5?.maxDecimalPlacesString(2)}%",
                style = MaterialTheme.typography.labelSmall,
                color = if ((tokenAccount.priceChange?.m5 ?: 0.0) > 0.0) primaryGreen else primaryRed,
            )
        }
    }
}

@Composable
@Preview
fun PriceChangeRowPreview(){
    PriceChangeRow(
        tokenAccount = TokenAccount(
            pubkey = "",
            name = "Solana",
            symbol = "SOL",
            uri = "",
            amount = "1.3",
            decimals = 9,
            amountDouble = 1.3,
            uiAmountString = "1.3",
            image = null,
            description = "Solana",
            createdOn = null,
            chainId = "solana",
            pairAddress = "",
            labels = emptyList(),
            priceNative = "1",
            priceUsd = "1",
            fdv = 0.0,
            marketCap = 0.0,
            pairCreatedAt = 0L,
            info = TokenMarketData(),
            baseToken = Token(
                address = "TODO()",
                name = "Solana",
                symbol = "SOL"
            ),
            quoteToken = Token(
                address = "TODO()",
                name = "Solana",
                symbol = "SOL"
            ),
            txns = Transactions(),
            volume = Volume(),
            priceChange = PriceChange(),
            liquidity = Liquidity()
        )
    )
}