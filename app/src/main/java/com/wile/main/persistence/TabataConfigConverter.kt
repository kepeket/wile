package com.wile.main.persistence

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.wile.main.model.TabataConfig

// FixMe : instantiating Gson each time we need it is a bad idea, it's quite expensive -> Injection
class TabataConfigConverter {
    @TypeConverter
    fun fromTabataConfig(value: TabataConfig?): String? {
        value?.let {
            val gson = Gson()
            return gson.toJson(it)
        }
        return null
    }

    @TypeConverter
    fun toTabataConfig(value: String?): TabataConfig? {
        value?.let {
            val gson = Gson()
            return gson.fromJson(it, TabataConfig::class.java)
        }
        return null
    }
}