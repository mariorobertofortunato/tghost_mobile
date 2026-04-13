package com.mrf.tghost.chain.solana.data.network.mappers

import com.mrf.tghost.chain.solana.data.network.model.SolanaTransactionDto
import com.mrf.tghost.domain.model.BalanceChange
import com.mrf.tghost.domain.model.Transaction
import com.mrf.tghost.domain.model.TransactionType

fun SolanaTransactionDto.toDomainModel(signature: String): Transaction {
    val meta = this.meta
    val accountKeys = this.transaction?.message?.accountKeys ?: emptyList()
    
    val balanceChanges = mutableListOf<BalanceChange>()

    // 1. SOL Balance Changes
    if (meta?.preBalances != null && meta.postBalances != null) {
        meta.preBalances.forEachIndexed { index, preBalance ->
            val postBalance = meta.postBalances.getOrNull(index) ?: preBalance
            val diff = postBalance - preBalance
            if (diff != 0L && index < accountKeys.size) {
                balanceChanges.add(
                    BalanceChange(
                        address = accountKeys[index],
                        amount = diff,
                        symbol = "SOL",
                        decimals = 9,
                        isNative = true
                    )
                )
            }
        }
    }

    // 2. Token Balance Changes
    val preTokenMap = meta?.preTokenBalances?.associateBy { it.accountIndex } ?: emptyMap()
    val postTokenMap = meta?.postTokenBalances?.associateBy { it.accountIndex } ?: emptyMap()
    
    val allTokenAccountIndices = (preTokenMap.keys + postTokenMap.keys).toSet()
    
    allTokenAccountIndices.forEach { index ->
        val preToken = preTokenMap[index]
        val postToken = postTokenMap[index]
        
        val preAmount = preToken?.uiTokenAmount?.amount?.toLongOrNull() ?: 0L
        val postAmount = postToken?.uiTokenAmount?.amount?.toLongOrNull() ?: 0L
        val diff = postAmount - preAmount
        
        if (diff != 0L) {
            val mint = postToken?.mint ?: preToken?.mint ?: "Unknown"
            val decimals = postToken?.uiTokenAmount?.decimals ?: preToken?.uiTokenAmount?.decimals ?: 0
            val address = postToken?.owner ?: (if (index < accountKeys.size) accountKeys[index] else "Unknown")
            
            balanceChanges.add(
                BalanceChange(
                    address = address,
                    amount = diff,
                    symbol = "Token", 
                    decimals = decimals,
                    isNative = false,
                    mint = mint
                )
            )
        }
    }

    // 3. Infer Type (Very basic heuristic)
    val type = when {
        balanceChanges.any { !it.isNative } -> TransactionType.TRANSFER // Could be SWAP, but simpler for now
        balanceChanges.filter { it.isNative }.size <= 3 -> TransactionType.TRANSFER
        else -> TransactionType.UNKNOWN
    }

    return Transaction(
        id = signature,
        chain = "solana",
        blockNumber = this.slot,
        timestamp = this.blockTime,
        fee = meta?.fee,
        isSuccess = meta?.err == null,
        error = meta?.err?.toString(),
        balanceChanges = balanceChanges,
        type = type
    )
}
