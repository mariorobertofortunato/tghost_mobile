package com.mrf.tghost.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mrf.tghost.domain.model.Chain
import com.mrf.tghost.domain.model.SupportedChainId
import kotlinx.serialization.Serializable

@Entity(tableName = "wallets_db")
data class WalletEntity (
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "publicKey") val publicKey: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "chainId") val chainId: SupportedChainId,
    @ColumnInfo(name = "snapshot") val snapshot: WalletEntitySnapshot?,
)

@Serializable
data class WalletEntitySnapshot(
    val timestamp: Long = System.currentTimeMillis(),
    val balanceUSd: Double = 0.0,
    val balanceNative: Double = 0.0
)