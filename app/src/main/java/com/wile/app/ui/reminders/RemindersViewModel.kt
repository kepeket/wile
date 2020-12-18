package com.wile.app.ui.reminders

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.wile.app.services.AlarmService
import com.wile.core.viewmodel.LiveCoroutinesViewModel
import com.wile.database.model.Reminder
import com.wile.database.model.ReminderDay
import com.wile.reminders.ReminderRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class RemindersViewModel @ViewModelInject constructor(
    private val reminderRepository: ReminderRepository,
    @Assisted private val savedStateHandle: SavedStateHandle,
    private var alarmService: AlarmService
) : LiveCoroutinesViewModel() {
    private val workoutId: Int =
        requireNotNull(savedStateHandle.get<Int>(RemindersActivity.WORKOUT_ID))
    private var _reminderLiveData: MutableLiveData<Reminder> =
        MutableLiveData(Reminder(workout = workoutId))
    val reminderLiveData: LiveData<Reminder> get() = _reminderLiveData

    init {
        viewModelScope.launch {
            reminderRepository.getReminderByWorkoutId(
                workout = workoutId,
                onSuccess = {},
                onError = {}
            ).collect {
                if (it != null) {
                    _reminderLiveData.value = it
                }
            }
        }
    }

    fun saveReminder() {
        reminderLiveData.value?.let {
            it.workout = workoutId
            if (it.active) {
                alarmService.setDailyAlarmForWorkout(it.workout, it.hour, it.minute)
            } else {
                alarmService.cancelDailyAlarmForWorkout(it.workout)
            }

            viewModelScope.launch {
                reminderRepository.saveReminder(
                    newReminder = it,
                    onSuccess = {},
                    onError = {}
                )
            }
        }
    }

    fun updateTime(hour: Int, minute: Int) {
        reminderLiveData.value?.let {
            it.hour = hour
            it.minute = minute
        }
    }

    fun setDay(day: ReminderDay, checked: Boolean) {
        when (day) {
            ReminderDay.Monday -> _reminderLiveData.value?.monday = checked
            ReminderDay.Tuesday -> _reminderLiveData.value?.tuesday = checked
            ReminderDay.Wednesday -> _reminderLiveData.value?.wednesday = checked
            ReminderDay.Thursday -> _reminderLiveData.value?.thursday = checked
            ReminderDay.Friday -> _reminderLiveData.value?.friday = checked
            ReminderDay.Saturday -> _reminderLiveData.value?.saturday = checked
            ReminderDay.Sunday -> _reminderLiveData.value?.sunday = checked
        }
    }

    fun setActive(checked: Boolean) {
        _reminderLiveData.value?.let {
            it.active = checked
        }
    }
}
