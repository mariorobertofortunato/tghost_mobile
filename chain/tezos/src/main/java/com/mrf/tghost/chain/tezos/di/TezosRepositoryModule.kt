package com.mrf.tghost.chain.tezos.di

import com.mrf.tghost.chain.tezos.data.repository.TezosStakingRepositoryImpl
import com.mrf.tghost.chain.tezos.data.repository.TezosTokenAccountsRepositoryImpl
import com.mrf.tghost.chain.tezos.data.repository.TezosTxRepositoryImpl
import com.mrf.tghost.chain.tezos.data.repository.TezosWalletBalanceRepositoryImpl
import com.mrf.tghost.chain.tezos.domain.repository.TezosStakingRepository
import com.mrf.tghost.chain.tezos.domain.repository.TezosTokenAccountsRepository
import com.mrf.tghost.chain.tezos.domain.repository.TezosTxRepository
import com.mrf.tghost.chain.tezos.domain.repository.TezosWalletBalanceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TezosRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindWalletBalanceRepository(impl: TezosWalletBalanceRepositoryImpl): TezosWalletBalanceRepository

    @Binds
    @Singleton
    abstract fun bindTokenAccountsRepository(impl: TezosTokenAccountsRepositoryImpl): TezosTokenAccountsRepository

    @Binds
    @Singleton
    abstract fun bindStakingRepository(impl: TezosStakingRepositoryImpl): TezosStakingRepository

    @Binds
    @Singleton
    abstract fun bindTransactionsRepository(impl: TezosTxRepositoryImpl): TezosTxRepository

}