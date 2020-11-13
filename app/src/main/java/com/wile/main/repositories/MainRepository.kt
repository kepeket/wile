package com.wile.main.repositories

import androidx.annotation.WorkerThread
import com.wile.main.model.Training
import com.wile.main.persistence.TrainingDao
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
    suspend fun addAll(trainings: List<Training>) = trainingDao.insertAll(trainings)
}