package com.mrf.tghost.chain.evm.di

import com.mrf.tghost.chain.evm.data.repository.EvmNftRepositoryImpl
import com.mrf.tghost.chain.evm.data.repository.EvmOnChainMetadataRepositoryImpl
import com.mrf.tghost.chain.evm.data.repository.EvmStakingRepositoryImpl
import com.mrf.tghost.chain.evm.data.repository.EvmTokenAccountsRepositoryImpl
import com.mrf.tghost.chain.evm.data.repository.EvmWalletBalanceRepositoryImpl
import com.mrf.tghost.chain.evm.domain.repository.EvmNftRepository
import com.mrf.tghost.chain.evm.domain.repository.EvmOnChainMetadataRepository
import com.mrf.tghost.chain.evm.domain.repository.EvmStakingRepository
import com.mrf.tghost.chain.evm.domain.repository.EvmTokenAccountsRepository
import com.mrf.tghost.chain.evm.domain.repository.EvmWalletBalanceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class EvmRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindWalletBalanceRepository(impl: EvmWalletBalanceRepositoryImpl): EvmWalletBalanceRepository

    @Binds
    @Singleton
    abstract fun bindTokenAccountsRepository(impl: EvmTokenAccountsRepositoryImpl): EvmTokenAccountsRepository

    @Binds
    @Singleton
    abstract fun bindStakingRepository(impl: EvmStakingRepositoryImpl): EvmStakingRepository

    @Binds
    @Singleton
    abstract fun bindNftRepository(impl: EvmNftRepositoryImpl): EvmNftRepository

    @Binds
    @Singleton
    abstract fun bindOnChainMetadataRepository(impl: EvmOnChainMetadataRepositoryImpl): EvmOnChainMetadataRepository


}