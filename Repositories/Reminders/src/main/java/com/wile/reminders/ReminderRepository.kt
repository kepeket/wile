package com.wile.reminders

import com.wile.database.model.Reminder
import kotlinx.coroutines.flow.Flow

interface ReminderRepository {

    fun getReminderByWorkoutId(
        workout: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ): Flow<Reminder?>

    fun hasReminderByWorkoutId(
        workout: Int
    ): Flow<Boolean>

    suspend fun saveReminder(
        newReminder: Reminder,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )

    suspend fun deleteReminder(id: Int)
}
