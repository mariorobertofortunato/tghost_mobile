package com.mrf.tghost.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mrf.tghost.data.database.entities.WalletEntity

@Database(entities = [WalletEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class TghostDatabase : RoomDatabase() {
    abstract val tghostDao: TghostDao
}