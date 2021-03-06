package com.wile.training

import com.wile.training.model.Preset
import com.wile.database.model.Training
import kotlinx.coroutines.flow.Flow

interface TrainingRepository {

    suspend fun getTraining(
        id: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ): Flow<Training>

    suspend fun saveTraining(
        newTraining: Training,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )

    fun fetchTrainingList(
        workout: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ): Flow<List<Training>>

    suspend fun fetchWorkoutIds(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ): Flow<List<Int>>

    suspend fun deleteTraining(id: Int)

    suspend fun deleteTrainings(workoutId: Int)

    suspend fun addAll(trainings: List<Training>)

    suspend fun addTrainingFromPreset(preset: Preset, workout: Int)

}
