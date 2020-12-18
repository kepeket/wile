package com.wile.app.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.wile.app.receivers.AlarmReceiver
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject

class AlarmService @Inject constructor(
    @ApplicationContext private val context: Context,
     private val alarmManager: AlarmManager?
) {

    private fun getChallengeIntent(workoutId: Int): PendingIntent {
        return Intent(context, AlarmReceiver::class.java).let { intent ->
            intent.putExtra(WORKOUT_ID_EXTRA, workoutId)
            PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    fun setDailyAlarmForWorkout(workoutId: Int, hour: Int, minute: Int) {
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }

        val alarmIntent = getChallengeIntent(workoutId)

        alarmManager?.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            1000 * 60 * 24,
            alarmIntent
        )
    }

    fun cancelDailyAlarmForWorkout(workoutId: Int) {
        val alarmIntent = getChallengeIntent(workoutId)

        alarmManager?.cancel(alarmIntent)
    }

    companion object {
        const val WORKOUT_ID_EXTRA = "alarm_workout_id"
    }
}