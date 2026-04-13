package com.mrf.tghost.chain.sui.domain.usecase

import com.mrf.tghost.chain.sui.domain.model.SuiOwnedObjects
import com.mrf.tghost.chain.sui.domain.repository.SuiOwnedObjectsRepository
import com.mrf.tghost.domain.model.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class GetOwnedObjectsUseCase @Inject constructor(
    private val ownedObjectsRepository: SuiOwnedObjectsRepository
) {

    fun suiOwnedObjects(publicKey: String): Flow<Result<SuiOwnedObjects>?> =
        ownedObjectsRepository.suiOwnedObjects(publicKey).onStart { emit(null) }

}