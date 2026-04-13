package com.mrf.tghost.app.contracts

import androidx.compose.runtime.Immutable
import com.mrf.tghost.app.ui.composables.ViewState
import com.mrf.tghost.domain.model.TokenAccount
import com.mrf.tghost.domain.model.Transaction
import com.mrf.tghost.domain.model.Wallet

@Immutable
data class WalletState(
    val wallet: Wallet? = null,
    val balanceNative: Double = 0.0,
    val balanceUSd: Double = 0.0,
    val tokenAccounts: List<TokenAccount> = emptyList(),
    val transactions: List<Transaction> = emptyList(),
    val walletViewState: ViewState = ViewState.None,
    val loadingStage: String? = null,
    val errorList: List<String> = emptyList()
)