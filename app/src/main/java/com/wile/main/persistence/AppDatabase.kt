package com.wile.main.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import com.wile.main.model.Training

@Database(entities = [
    Training::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trainingDao(): TrainingDao
}
