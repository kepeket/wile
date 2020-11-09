package com.wile.main.repository

import androidx.annotation.WorkerThread
import com.wile.main.model.Training
import com.wile.main.persistence.TrainingDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AddRepository @Inject constructor(
    private val trainingDao: TrainingDao
) : Repository {

    @WorkerThread
    suspend fun saveTraining(
        newTraining: Training,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) = flow {
        val training = trainingDao.insertTrainingList(newTraining)
        emit(training)
    }.flowOn(Dispatchers.IO)
}