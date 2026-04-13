package com.mrf.tghost.chain.solana.di

import com.mrf.tghost.chain.solana.data.network.websocket.SolanaWsCoordinatorImpl
import com.mrf.tghost.chain.solana.domain.network.SolanaWsCoordinator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SolanaNetworkModule {

    @Binds
    @Singleton
    abstract fun bindSolanaWsCoordinator(impl: SolanaWsCoordinatorImpl): SolanaWsCoordinator

}