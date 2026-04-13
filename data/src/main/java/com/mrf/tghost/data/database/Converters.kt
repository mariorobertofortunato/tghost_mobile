package com.mrf.tghost.data.database

import androidx.room.TypeConverter
import com.mrf.tghost.data.database.entities.WalletEntitySnapshot
import com.mrf.tghost.domain.model.SupportedChainId
import kotlinx.serialization.json.Json

object Converters {

    @TypeConverter
    @JvmStatic
    fun fromChainId(chain: SupportedChainId?): String? {
        return chain?.name
    }

    @TypeConverter
    @JvmStatic
    fun toChainId(value: String?): SupportedChainId? {
        return value?.let {
            try {
                SupportedChainId.valueOf(it)
            } catch (e: Exception) {
                null
            }
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromSnapshot(snapshot: WalletEntitySnapshot?): String? {
        return snapshot?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    @JvmStatic
    fun toSnapshot(value: String?): WalletEntitySnapshot? {
        return value?.let { Json.decodeFromString(it) }
    }
}
