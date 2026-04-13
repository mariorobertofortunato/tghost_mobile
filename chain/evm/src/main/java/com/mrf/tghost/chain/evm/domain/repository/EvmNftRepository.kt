package com.mrf.tghost.chain.evm.domain.repository

import com.mrf.tghost.domain.model.Result
import com.mrf.tghost.chain.evm.domain.model.EvmNftResponse
import kotlinx.coroutines.flow.Flow

interface EvmNftRepository {
    fun evmNFTSAccounts(publicKey: String, evmChainId: String): Flow<Result<EvmNftResponse>?>
}