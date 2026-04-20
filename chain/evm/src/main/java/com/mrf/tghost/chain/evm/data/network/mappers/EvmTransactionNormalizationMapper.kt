package com.mrf.tghost.chain.evm.data.network.mappers

import com.mrf.tghost.domain.model.Transaction

fun List<Transaction>.normalizeEvmTransactions(): List<Transaction> =
    distinctBy { "${it.chain}:${it.id}:${it.timestamp}:${it.blockNumber}" }
        .sortedWith(
            compareByDescending<Transaction> { it.timestamp ?: Long.MIN_VALUE }
                .thenByDescending { it.blockNumber ?: Long.MIN_VALUE }
                .thenByDescending { it.id },
        )
