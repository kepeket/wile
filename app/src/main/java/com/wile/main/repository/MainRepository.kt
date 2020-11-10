package com.wile.main.repository

import androidx.annotation.WorkerThread
import com.wile.main.persistence.TrainingDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val trainingDao: TrainingDao
) : Repository {

    @WorkerThread
    fun fetchTrainingList(
        workout: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) = trainingDao.getTrainingList(workout)

    @WorkerThread
    suspend fun deleteTraining(id: Int) = trainingDao.delete(id)

    @WorkerThread
    suspend fun fetchTrainingDuration(
            workout: Int,
            onSuccess: () -> Unit,
            onError: (String) -> Unit
    ) = flow {
        var trainingDuration = trainingDao.getTrainingDuration(workout)
        emit(trainingDuration)
    }.flowOn(Dispatchers.IO)
}