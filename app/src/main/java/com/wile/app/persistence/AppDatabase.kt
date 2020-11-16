package com.wile.app.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.wile.app.model.Training

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
