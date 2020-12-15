package com.wile.database.converters

import androidx.room.TypeConverter
import java.sql.Time

class TimeConverter {
    @TypeConverter
    fun fromTime(value: Time): String {
        return value.toString()
    }

    @TypeConverter
    fun toTime(value: String): Time {
        return Time.valueOf(value)
    }
}