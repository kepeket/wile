package com.wile.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.wile.database.converters.SealedClassConverter
import com.wile.database.converters.TabataConfigConverter
import com.wile.database.dao.TrainingDao
import com.wile.database.model.Training

@Database(entities = [
    Training::class],
    version = 4
)
@TypeConverters(
    SealedClassConverter::class,
    TabataConfigConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trainingDao(): TrainingDao
}
