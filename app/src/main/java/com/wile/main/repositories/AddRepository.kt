package com.wile.main.repositories

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
    suspend fun getTraining(
            id: Int,
            onSuccess: () -> Unit,
            onError: (String) -> Unit
    ) = flow {
            emit(trainingDao.getTraining(id))
        }.flowOn(Dispatchers.IO)

    @WorkerThread
    suspend fun saveTraining(
        newTraining: Training,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )  {
        val training = trainingDao.insertTrainingList(newTraining)
    }
}