package com.wile.app.di

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Vibrator
import android.view.inputmethod.InputMethodManager
import androidx.core.app.NotificationCompat
import androidx.preference.PreferenceManager
import com.wile.app.services.AlarmService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideVibrator(
        @ApplicationContext context: Context
    ) = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?

    @Provides
    @Singleton
    fun provideAlarm(
        @ApplicationContext context: Context
    ) = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?


    @Provides
    @Singleton
    fun provideInputMethodManager(
        @ApplicationContext context: Context
    ) = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?

    @Provides
    fun provideSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    @Provides
    fun provideNotificationManager(
        @ApplicationContext context: Context
    ) =  context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
}
