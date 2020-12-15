package com.wile.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.wile.database.converters.SealedClassConverter
import com.wile.database.converters.TabataConfigConverter
import com.wile.database.converters.TimeConverter
import com.wile.database.dao.ReminderDao
import com.wile.database.dao.TrainingDao
import com.wile.database.model.Reminder
import com.wile.database.model.Training
import kotlin.time.ExperimentalTime

@Database(entities = [
    Training::class, Reminder::class],
    version = 7
)
@TypeConverters(
    SealedClassConverter::class,
    TabataConfigConverter::class,
    TimeConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trainingDao(): TrainingDao
    abstract fun reminderDao(): ReminderDao
}
