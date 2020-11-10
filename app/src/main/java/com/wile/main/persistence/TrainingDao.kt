package com.wile.main.persistence


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wile.main.model.Training
import kotlinx.coroutines.flow.Flow

@Dao
interface TrainingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrainingList(training: Training)

    @Query("SELECT * FROM Training WHERE workout = :workout_")
    fun getTrainingList(workout_: Int): Flow<List<Training>>

    @Query("SELECT SUM(duration) FROM Training WHERE workout = :workout_")
    suspend fun getTrainingDuration(workout_: Int): Int
}