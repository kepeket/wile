package com.wile.app.receivers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.net.Uri
import com.wile.app.R
import com.wile.app.services.AlarmService
import com.wile.app.ui.workout.WorkoutActivity
import com.wile.core.receivers.HiltBroadcastReceiver
import com.wile.reminders.ReminderRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : HiltBroadcastReceiver() {

    @Inject
    lateinit var reminderRepository: ReminderRepository

    var notificationManager: NotificationManager? = null
        @Inject set

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val workoutId: Int? = intent.extras?.getInt(AlarmService.WORKOUT_ID_EXTRA, -1)

        workoutId?.let { id ->
            if (id >= 0) {
                GlobalScope.launch {
                    reminderRepository.getReminderByWorkoutId(id,
                        onSuccess = {},
                        onError = {}).collect { reminder ->
                        reminder?.let {
                            if (!it.active) {
                                return@collect
                            }
                            when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
                                Calendar.MONDAY -> {
                                    if (it.monday) notifyWorkout(context, id)
                                }
                                Calendar.TUESDAY -> {
                                    if (it.tuesday) notifyWorkout(context, id)
                                }
                                Calendar.WEDNESDAY -> {
                                    if (it.wednesday) notifyWorkout(context, id)
                                }
                                Calendar.THURSDAY -> {
                                    if (it.thursday) notifyWorkout(context, id)
                                }
                                Calendar.FRIDAY -> {
                                    if (it.friday) notifyWorkout(context, id)
                                }
                                Calendar.SATURDAY -> {
                                    if (it.saturday) notifyWorkout(context, id)
                                }
                                Calendar.SUNDAY -> {
                                    if (it.sunday) notifyWorkout(context, id)
                                }
                                else -> {
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun notifyWorkout(context: Context?, workoutId: Int) {
        context?.let { ctx ->
            val soundUri = Uri.parse(
                ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                    context.applicationContext.packageName + "/" + R.raw.whistle)
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            notificationManager?.createNotificationChannel(
                NotificationChannel(
                    "workout_reminder",
                    context.getString(R.string.alarm_notification_channel),
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    enableVibration(true)
                    vibrationPattern = longArrayOf(100, 100)
                    setSound(soundUri, audioAttributes)
                }
            )

            val mainIntent = WorkoutActivity.startWorkout(ctx, workoutId).let { intent ->
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                PendingIntent.getActivity(
                    ctx,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                );
            }

            val notification: Notification = Notification.Builder(ctx, "workout_reminder")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                    ctx.resources,
                    R.drawable.ic_baseline_add_alarm_24))
                .setContentTitle(context.getString(R.string.alarm_notification_title))
                .setContentText(context.getString(R.string.alarm_notification_sub_text))
                .setStyle(Notification.BigTextStyle().apply {
                    bigText(context.getString(R.string.alarm_notification_text))
                })
                .setColorized(true)
                .setColor(ctx.getColor(R.color.training_go_blue))
                .setContentIntent(mainIntent)
                .setCategory(Notification.CATEGORY_REMINDER)
                .build()

            notificationManager?.notify(workoutId, notification);
        }
    }
}