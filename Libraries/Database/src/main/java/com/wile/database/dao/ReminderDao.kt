package com.wile.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wile.database.model.Reminder
import com.wile.database.model.Training
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder: Reminder)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(reminder: List<Reminder>)

    @Query("SELECT * FROM Reminder WHERE workout = :workout")
    fun getWorkoutReminders(workout: Int): Flow<List<Reminder>>

    @Query("SELECT * FROM Reminder WHERE id = :id")
    fun getReminder(id: Int): Reminder

    @Query("DELETE FROM Reminder WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("DELETE FROM Reminder WHERE workout = :workoutId")
    suspend fun deleteByWorkoutId(workoutId: Int)

}
