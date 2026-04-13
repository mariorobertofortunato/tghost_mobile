package com.mrf.tghost.app.di

import android.app.Application
import androidx.room.Room
import com.mrf.tghost.data.database.TghostDao
import com.mrf.tghost.data.database.TghostDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DBModule {

    @Provides
    @Singleton
    fun provideDatabase(app: Application): TghostDatabase {
        return Room.databaseBuilder(app, TghostDatabase::class.java, "database")
            .fallbackToDestructiveMigration(false)
            .build()
    }

    @Singleton
    @Provides
    fun provideDao(database: TghostDatabase): TghostDao {
        return database.tghostDao
    }
}