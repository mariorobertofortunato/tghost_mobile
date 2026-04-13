package com.mrf.tghost.app.di

import com.mrf.tghost.data.repository.LocalWalletRepositoryImpl
import com.mrf.tghost.data.repository.MarketDataRepositoryImpl
import com.mrf.tghost.data.repository.OffChainMetadataRepositoryImpl
import com.mrf.tghost.data.repository.PreferencesRepositoryImpl
import com.mrf.tghost.domain.repository.LocalWalletRepository
import com.mrf.tghost.domain.repository.MarketDataRepository
import com.mrf.tghost.domain.repository.OffChainMetadataRepository
import com.mrf.tghost.domain.repository.PreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPreferencesRepository(
        impl: PreferencesRepositoryImpl
    ): PreferencesRepository

    @Binds
    @Singleton
    abstract fun bindLocalWalletRepository(
        localWalletRepositoryImpl: LocalWalletRepositoryImpl
    ): LocalWalletRepository

    @Binds
    @Singleton
    abstract fun bindMarketDataRepository(impl: MarketDataRepositoryImpl): MarketDataRepository

    @Binds
    @Singleton
    abstract fun bindOffChainMetadataRepository(impl: OffChainMetadataRepositoryImpl): OffChainMetadataRepository

}