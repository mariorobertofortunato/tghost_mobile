package com.mrf.tghost.chain.solana.domain.network

import com.mrf.tghost.chain.solana.domain.model.DasApiResponse
import com.mrf.tghost.chain.solana.domain.model.SolanaSplTokenAccount
import com.mrf.tghost.chain.solana.domain.model.SolanaStake
import kotlinx.coroutines.flow.Flow
import com.mrf.tghost.domain.model.Result

/**
 * Single consumer for Solana WebSocket: multiplexes subscriptions (account, program, nft)
 * and routes messages to the correct Flow. Repositories use this instead of
 * [com.mrf.tghost.domain.network.WebSocketManager].messageFlow() directly to avoid N×4 collectors on the same stream.
 */
interface SolanaWsCoordinator {
    fun subscribeAccountBalance(publicKey: String): Flow<Result<Long>>
    fun subscribeTokenAccounts(publicKey: String): Flow<Result<List<SolanaSplTokenAccount>>>
    fun subscribeStakingAccounts(walletAddress: String): Flow<Result<List<SolanaStake>>>
    fun subscribeNftAccounts(publicKey: String): Flow<Result<DasApiResponse>>
}