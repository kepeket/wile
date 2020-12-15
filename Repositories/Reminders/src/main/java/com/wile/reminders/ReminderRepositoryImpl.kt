package com.wile.reminders

import androidx.annotation.WorkerThread
import androidx.lifecycle.MutableLiveData
import com.wile.database.dao.ReminderDao
import com.wile.database.model.Reminder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@ExperimentalCoroutinesApi
class ReminderRepositoryImpl @Inject constructor(
    private val reminderDao: ReminderDao
) : ReminderRepository {

    @WorkerThread
    override fun getReminderByWorkoutId(
        workout: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) = flow {
            emit(reminderDao.getReminderByWorkoutId(workout))
        }.flowOn(Dispatchers.IO)

    @WorkerThread
    override fun hasReminderByWorkoutId(workout: Int): Flow<Boolean> = flow {
        val reminder = reminderDao.getReminderByWorkoutId(workout)
        @Suppress("SENSELESS_COMPARISON")
        if (reminder != null){
            emit(reminder.workout == workout && reminder.active)
        } else {
            emit(false)
        }

    }.flowOn(Dispatchers.IO)

    @WorkerThread
    override suspend fun saveReminder(
        newReminder: Reminder,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        reminderDao.insert(newReminder)
    }

    @WorkerThread
    override suspend fun deleteReminder(id: Int) = reminderDao.delete(id)
}
