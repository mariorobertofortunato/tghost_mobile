package com.mrf.tghost.app.ui.mapper

import androidx.annotation.DrawableRes
import com.mrf.tghost.R
import com.mrf.tghost.app.contracts.WalletState
import com.mrf.tghost.domain.model.EvmChain
import com.mrf.tghost.domain.model.SupportedChain
import com.mrf.tghost.domain.model.TransactionType

@DrawableRes
fun getChainIcon(symbol: String?): Int? {
    return when (symbol) {
        "SOL", "stSOL", "WSOL" -> R.drawable.logo_sol
        "ETH", "stETH", "WETH" -> R.drawable.logo_eth
        "SUI", "stSUI" -> R.drawable.logo_sui
        "TEZ", "stXTZ", "XTZ" -> R.drawable.logo_tez
        else -> null
    }
}


fun getChainIconBadge(chainId: String?): Int? {
    return when (chainId) {
        EvmChain.ETHEREUM.chain, "ETHEREUM", "ethereum" -> {
            R.drawable.logo_chain_ethereum_trx_24
        }

        EvmChain.BASE.chain -> {
            R.drawable.logo_chain_base_trx_24
        }

        else -> {
            null
        }
    }
}

fun getWalletIcon(walletState: WalletState): Int? {
    val supportedChain = SupportedChain.entries.find { it.chain.id == walletState.wallet?.chainId }
    return getChainIcon(supportedChain?.chain?.symbol)
}

@DrawableRes
fun getTransactionTypeIcon(type: TransactionType): Int {
    return when (type) {
        TransactionType.TRANSFER -> R.drawable.ic_transactions
        TransactionType.SWAP -> R.drawable.ic_swap
        TransactionType.STAKE -> R.drawable.ic_savings
        TransactionType.UNSTAKE -> R.drawable.ic_money_out
        TransactionType.CONTRACT_INTERACTION -> R.drawable.ic_adjust
        TransactionType.UNKNOWN -> R.drawable.ic_help
    }
}
