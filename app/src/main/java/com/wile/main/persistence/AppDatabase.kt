package com.wile.main.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.wile.main.model.Training

@Database(entities = [
    Training::class],
    version = 2
)
@TypeConverters(SealedClassConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trainingDao(): TrainingDao
}
