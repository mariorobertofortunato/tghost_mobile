package com.mrf.tghost.chain.evm.domain.repository

import com.mrf.tghost.domain.model.Result
import com.mrf.tghost.chain.evm.domain.model.EvmNftResponse
import com.mrf.tghost.domain.model.EvmChain
import kotlinx.coroutines.flow.Flow

interface EvmNftRepository {
    fun evmNFTSAccounts(publicKey: String, evmChainId: EvmChain?): Flow<Result<EvmNftResponse>?>
}