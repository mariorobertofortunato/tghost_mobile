package com.mrf.tghost.chain.sui.domain.repository

import com.mrf.tghost.chain.sui.domain.model.SuiOwnedObjects
import com.mrf.tghost.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface SuiOwnedObjectsRepository {
    fun suiOwnedObjects(publicKey: String): Flow<Result<SuiOwnedObjects>?>
}
