package com.mrf.tghost.app.di

import com.mrf.tghost.data.network.websocket.core.WebSocketManagerImpl
import com.mrf.tghost.domain.network.WebSocketManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

    @Binds
    @Singleton
    abstract fun bindWebSocketManager(impl: WebSocketManagerImpl): WebSocketManager

}
