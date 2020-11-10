package com.wile.main.persistence


import androidx.room.*
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

    @Query("DELETE FROM Training WHERE id = :id_")
    suspend fun delete(id_: Int)

}