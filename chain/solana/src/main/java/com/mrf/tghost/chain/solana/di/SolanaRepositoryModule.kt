package com.mrf.tghost.chain.solana.di

import com.mrf.tghost.chain.solana.data.repository.SolanaNftRepositoryImpl
import com.mrf.tghost.chain.solana.data.repository.SolanaOnChainMetadataRepositoryImpl
import com.mrf.tghost.chain.solana.data.repository.SolanaStakingRepositoryImpl
import com.mrf.tghost.chain.solana.data.repository.SolanaTokenAccountsRepositoryImpl
import com.mrf.tghost.chain.solana.data.repository.SolanaTxRepositoryImpl
import com.mrf.tghost.chain.solana.data.repository.SolanaWalletBalanceRepositoryImpl
import com.mrf.tghost.chain.solana.domain.repository.SolanaNftRepository
import com.mrf.tghost.chain.solana.domain.repository.SolanaOnChainMetadataRepository
import com.mrf.tghost.chain.solana.domain.repository.SolanaStakingRepository
import com.mrf.tghost.chain.solana.domain.repository.SolanaTokenAccountsRepository
import com.mrf.tghost.chain.solana.domain.repository.SolanaTxRepository
import com.mrf.tghost.chain.solana.domain.repository.SolanaWalletBalanceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SolanaRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindWalletBalanceRepository(impl: SolanaWalletBalanceRepositoryImpl): SolanaWalletBalanceRepository

    @Binds
    @Singleton
    abstract fun bindTokenAccountsRepository(impl: SolanaTokenAccountsRepositoryImpl): SolanaTokenAccountsRepository

    @Binds
    @Singleton
    abstract fun bindStakingRepository(impl: SolanaStakingRepositoryImpl): SolanaStakingRepository

    @Binds
    @Singleton
    abstract fun bindNftRepository(impl: SolanaNftRepositoryImpl): SolanaNftRepository

    @Binds
    @Singleton
    abstract fun bindOnChainMetadataRepository(impl: SolanaOnChainMetadataRepositoryImpl): SolanaOnChainMetadataRepository

    @Binds
    @Singleton
    abstract fun bindTxRepository(impl: SolanaTxRepositoryImpl): SolanaTxRepository

}
