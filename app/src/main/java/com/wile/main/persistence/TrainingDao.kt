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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(training: List<Training>)

    @Query("SELECT * FROM Training WHERE workout = :workout ORDER BY sorting ASC")
    fun getTrainingList(workout: Int): Flow<List<Training>>

    @Query("SELECT * FROM Training WHERE id = :id")
    fun getTraining(id: Int): Training

    @Query("DELETE FROM Training WHERE id = :id")
    suspend fun delete(id: Int)

}
