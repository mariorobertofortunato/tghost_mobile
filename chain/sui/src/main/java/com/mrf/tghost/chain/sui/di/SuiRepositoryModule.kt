package com.mrf.tghost.chain.sui.di

import com.mrf.tghost.chain.sui.data.repository.SuiOwnedObjectsRepositoryImpl
import com.mrf.tghost.chain.sui.data.repository.SuiOnChainMetadataRepositoryImpl
import com.mrf.tghost.chain.sui.data.repository.SuiStakingRepositoryImpl
import com.mrf.tghost.chain.sui.data.repository.SuiWalletActivityRepositoryImpl
import com.mrf.tghost.chain.sui.domain.repository.SuiOwnedObjectsRepository
import com.mrf.tghost.chain.sui.domain.repository.SuiOnChainMetadataRepository
import com.mrf.tghost.chain.sui.domain.repository.SuiStakingRepository
import com.mrf.tghost.chain.sui.domain.repository.SuiWalletActivityRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SuiRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindStakingRepository(impl: SuiStakingRepositoryImpl): SuiStakingRepository

    @Binds
    @Singleton
    abstract fun bindNftRepository(impl: SuiOwnedObjectsRepositoryImpl): SuiOwnedObjectsRepository

    @Binds
    @Singleton
    abstract fun bindOnChainMetadataRepository(impl: SuiOnChainMetadataRepositoryImpl): SuiOnChainMetadataRepository

    @Binds
    @Singleton
    abstract fun bindWalletActivityRepository(impl: SuiWalletActivityRepositoryImpl): SuiWalletActivityRepository

}