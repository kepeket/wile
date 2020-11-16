package com.wile.main.persistence

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.wile.main.model.TabataConfig

@ProvidedTypeConverter
class TabataConfigConverter(
    private val gson: Gson
) {
    @TypeConverter
    fun fromTabataConfig(value: TabataConfig?): String? {
        value?.let {
            return gson.toJson(it)
        }
        return null
    }

    @TypeConverter
    fun toTabataConfig(value: String?): TabataConfig? {
        value?.let {
            return gson.fromJson(it, TabataConfig::class.java)
        }
        return null
    }
}