package com.wile.reminders

import androidx.annotation.WorkerThread
import com.wile.training.model.Preset
import com.wile.database.model.Training
import com.wile.database.model.TrainingTypes
import com.wile.database.dao.TrainingDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

// FixMe : miss a Network stack
class ReminderRepositoryImpl @Inject constructor(
    private val trainingDao: TrainingDao
) : TrainingRepository {

    @ExperimentalCoroutinesApi
    @WorkerThread
    override suspend fun getTraining(
            id: Int,
            onSuccess: () -> Unit,
            onError: (String) -> Unit
    ) = flow {
            emit(trainingDao.getTraining(id))
        }.flowOn(Dispatchers.IO)

    @WorkerThread
    override suspend fun saveTraining(
        newTraining: Training,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        trainingDao.insertTraining(newTraining)
    }

    @WorkerThread
    override fun fetchTrainingList(
        workout: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) = trainingDao.getTrainingList(workout)

    @WorkerThread
    override suspend fun fetchWorkoutIds(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) = trainingDao.getWorkoutIds()

    @WorkerThread
    override suspend fun deleteTraining(id: Int) = trainingDao.delete(id)

    @WorkerThread
    override suspend fun deleteTrainings(workoutId: Int) = trainingDao.deleteByWorkoutId(workoutId)

    @WorkerThread
    override suspend fun addAll(trainings: List<Training>) = trainingDao.insertAll(trainings)

    @WorkerThread
    override suspend fun addTrainingFromPreset(preset: Preset, workout: Int)  {
        val training = Training(
            name = preset.name,
            trainingType = preset.trainingType,
            workout = workout
        )

        when(preset.trainingType){
            TrainingTypes.Timed -> {
                training.duration = preset.duration
            }
            TrainingTypes.Repeated -> {
                training.reps = preset.reps
                training.customRepRate = true
                training.repRate = preset.repRate
            }
            else -> TODO()
        }

        trainingDao.insertTraining(training)
    }
}
