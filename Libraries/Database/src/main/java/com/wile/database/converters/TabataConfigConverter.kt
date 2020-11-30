package com.wile.database.converters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.squareup.moshi.JsonAdapter
import com.wile.database.model.TabataConfig
import dagger.Reusable
import javax.inject.Inject

@ProvidedTypeConverter
@Reusable
class TabataConfigConverter @Inject constructor(
    private val adapter: JsonAdapter<TabataConfig>
) {
    @TypeConverter
    fun fromTabataConfig(value: TabataConfig?): String? {
        value?.let {
            return adapter.toJson(it)
        }
        return null
    }

    @TypeConverter
    fun toTabataConfig(value: String?): TabataConfig? {
        value?.let {
            return adapter.fromJson(it)
        }
        return null
    }
}