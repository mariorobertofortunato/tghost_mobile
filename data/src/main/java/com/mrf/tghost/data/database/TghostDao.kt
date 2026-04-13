package com.mrf.tghost.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mrf.tghost.data.database.entities.WalletEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TghostDao {

    @Query("SELECT * FROM wallets_db ORDER BY name ASC")
    fun getWallets(): Flow<List<WalletEntity>>

    @Query("SELECT * FROM wallets_db WHERE publicKey = :publicKey")
    fun getWallet(publicKey: String): WalletEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNewWallet (walletEntity: WalletEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateWallet (walletEntity: WalletEntity)

    @Query("DELETE FROM wallets_db WHERE publicKey = :publicKey")
    suspend fun deleteWallet (publicKey: String)

}
