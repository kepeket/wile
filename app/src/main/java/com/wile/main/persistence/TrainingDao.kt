package com.wile.main.persistence


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wile.main.model.Training

@Dao
interface TrainingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrainingList(training: Training)

    @Query("SELECT * FROM Training WHERE workout = :workout_")
    suspend fun getTrainingList(workout_: Int): List<Training>

    @Query("SELECT SUM(duration) FROM Training WHERE workout = :workout_")
    suspend fun getTrainingDuration(workout_: Int): Int
}