package com.mrf.tghost.chain.solana.data.network.mappers

import com.mrf.tghost.chain.solana.data.network.model.SolanaTransactionDto
import com.mrf.tghost.chain.solana.utils.SOLANA_KNOWN_MINT_SYMBOLS
import com.mrf.tghost.chain.solana.utils.SOLANA_SWAP_PROGRAM_IDS
import com.mrf.tghost.chain.solana.utils.SOLANA_STAKE_PROGRAM_ID
import com.mrf.tghost.domain.model.BalanceChange
import com.mrf.tghost.domain.model.Transaction
import com.mrf.tghost.domain.model.TransactionType
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlin.math.abs

fun SolanaTransactionDto.toDomainModel(signature: String, walletAddress: String): Transaction {
    val meta = this.meta
    val accountKeys = extractAccountKeys(this)
    val normalizedWallet = walletAddress.lowercase()

    val balanceChanges = mutableListOf<BalanceChange>()

    // 1. SOL Balance Changes (wallet-centric)
    if (meta?.preBalances != null && meta.postBalances != null) {
        meta.preBalances.forEachIndexed { index, preBalance ->
            val postBalance = meta.postBalances.getOrNull(index) ?: preBalance
            val diff = postBalance - preBalance
            val account = accountKeys.getOrNull(index).orEmpty()
            if (diff != 0L && account.lowercase() == normalizedWallet) {
                balanceChanges.add(
                    BalanceChange(
                        address = account,
                        amount = diff,
                        symbol = "SOL",
                        decimals = 9,
                        isNative = true
                    )
                )
            }
        }
    }

    // 2. Token Balance Changes (wallet-centric)
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
            val owner = postToken?.owner ?: preToken?.owner
            val accountAtIndex = accountKeys.getOrNull(index).orEmpty()
            val belongsToWallet = owner?.lowercase() == normalizedWallet ||
                    accountAtIndex.lowercase() == normalizedWallet

            if (!belongsToWallet) return@forEach

            balanceChanges.add(
                BalanceChange(
                    address = owner ?: walletAddress,
                    amount = diff,
                    symbol = resolveTokenSymbol(mint),
                    decimals = decimals,
                    isNative = false,
                    mint = mint
                )
            )
        }
    }
    val netBalanceChanges = netBalanceChanges(balanceChanges)
        .filterNot { isDustChange(it) }

    // 3. Infer Type
    val programIds = extractProgramIds(this)
    val type = inferTransactionType(
        programIds = programIds,
        logs = meta?.logMessages.orEmpty(),
        balanceChanges = netBalanceChanges
    )

    return Transaction(
        id = signature,
        chain = "solana",
        blockNumber = this.slot,
        timestamp = this.blockTime,
        fee = meta?.fee,
        isSuccess = meta?.err == null,
        error = meta?.err?.toString(),
        balanceChanges = netBalanceChanges,
        type = type
    )
}

private fun netBalanceChanges(changes: List<BalanceChange>): List<BalanceChange> {
    if (changes.isEmpty()) return emptyList()
    val grouped = linkedMapOf<String, BalanceChange>()
    changes.forEach { change ->
        val key = if (change.isNative) {
            "native:${change.address}"
        } else {
            "token:${change.mint}:${change.address}"
        }
        val current = grouped[key]
        if (current == null) {
            grouped[key] = change
        } else {
            grouped[key] = current.copy(amount = current.amount + change.amount)
        }
    }
    return grouped.values.filter { it.amount != 0L }
}

private fun isDustChange(change: BalanceChange): Boolean {
    return if (change.isNative) {
        abs(change.amount) <= SOL_DUST_LAMPORT_THRESHOLD
    } else {
        abs(change.amount) <= TOKEN_DUST_BASE_UNITS_THRESHOLD
    }
}

private fun extractProgramIds(tx: SolanaTransactionDto): Set<String> {
    val accountKeys = extractAccountKeys(tx)
    val topLevelProgramIds = tx.transaction?.message?.instructions
        .orEmpty()
        .mapNotNull { instruction -> extractProgramId(instruction, accountKeys) }
    val innerProgramIds = tx.meta?.innerInstructions
        .orEmpty()
        .flatMap { inner -> extractInnerProgramIds(inner, accountKeys) }
    return (topLevelProgramIds + innerProgramIds)
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .toSet()
}

private fun extractAccountKeys(tx: SolanaTransactionDto): List<String> {
    return tx.transaction?.message?.accountKeys
        .orEmpty()
        .mapNotNull { key ->
            when (key) {
                is JsonPrimitive -> key.contentOrNull
                is JsonObject -> key["pubkey"]?.let { it as? JsonPrimitive }?.contentOrNull
                else -> null
            }
        }
}

private fun extractProgramId(instruction: JsonElement, accountKeys: List<String>): String? {
    val obj = instruction as? JsonObject ?: return null
    val directProgramId = (obj["programId"] as? JsonPrimitive)?.contentOrNull
    if (!directProgramId.isNullOrBlank()) return directProgramId

    val programIdIndex = (obj["programIdIndex"] as? JsonPrimitive)?.intOrNull
    return programIdIndex?.let { accountKeys.getOrNull(it) }
}

private fun extractInnerProgramIds(innerInstruction: JsonElement, accountKeys: List<String>): List<String> {
    val obj = innerInstruction as? JsonObject ?: return emptyList()
    val instructions = obj["instructions"] as? JsonArray ?: return emptyList()
    return instructions.mapNotNull { extractProgramId(it, accountKeys) }
}

private fun inferTransactionType(
    programIds: Set<String>,
    logs: List<String>,
    balanceChanges: List<BalanceChange>
): TransactionType {
    if (balanceChanges.isEmpty()) return TransactionType.UNKNOWN

    val hasPositiveToken = balanceChanges.any { !it.isNative && it.amount > 0 }
    val hasNegativeToken = balanceChanges.any { !it.isNative && it.amount < 0 }
    val hasTokenBalanceChange = balanceChanges.any { !it.isNative }
    val netNativeDelta = balanceChanges.filter { it.isNative }.sumOf { it.amount }
    val loweredLogs = logs.map { it.lowercase() }

    val hasSwapHint = programIds.any { it in SOLANA_SWAP_PROGRAM_IDS } ||
            loweredLogs.any { log ->
                SWAP_LOG_KEYWORDS.any { keyword -> keyword in log }
            } ||
            (hasPositiveToken && hasNegativeToken)
    if (hasSwapHint) {
        return TransactionType.SWAP
    }

    val hasStakeHint = programIds.contains(SOLANA_STAKE_PROGRAM_ID) ||
            loweredLogs.any { log ->
                STAKE_LOG_KEYWORDS.any { keyword -> keyword in log }
            }
    if (hasStakeHint) {
        return if (netNativeDelta > 0L) TransactionType.UNSTAKE else TransactionType.STAKE
    }

    if (hasTokenBalanceChange || balanceChanges.size <= 3) {
        return TransactionType.TRANSFER
    }

    return if (programIds.isNotEmpty()) {
        TransactionType.CONTRACT_INTERACTION
    } else {
        TransactionType.UNKNOWN
    }
}

private fun resolveTokenSymbol(mint: String): String {
    if (mint.isBlank() || mint == "Unknown") return "SPL"
    return SOLANA_KNOWN_MINT_SYMBOLS[mint] ?: mint.toShortAddressLabel()
}

private fun String.toShortAddressLabel(): String {
    if (length <= 10) return this
    return "${take(4)}...${takeLast(4)}"
}

private val SWAP_LOG_KEYWORDS = setOf(
    "instruction: swap",
    "jupiter",
    "raydium",
    "orca"
)

private val STAKE_LOG_KEYWORDS = setOf(
    "instruction: delegate",
    "instruction: deactivate",
    "instruction: withdraw",
    "instruction: split"
)

private const val SOL_DUST_LAMPORT_THRESHOLD = 10_000L
private const val TOKEN_DUST_BASE_UNITS_THRESHOLD = 0L
