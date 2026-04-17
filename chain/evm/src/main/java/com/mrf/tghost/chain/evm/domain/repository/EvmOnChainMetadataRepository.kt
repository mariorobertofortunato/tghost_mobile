package com.mrf.tghost.chain.evm.domain.repository

import com.mrf.tghost.domain.model.EvmChain
import com.mrf.tghost.domain.model.Result
import com.mrf.tghost.chain.evm.domain.model.EvmMetadataResponse
import kotlinx.coroutines.flow.Flow

interface EvmOnChainMetadataRepository {
    fun getEvmTokenOnChainMetadata(address: String, evmChain: EvmChain?): Flow<Result<EvmMetadataResponse?>>
}