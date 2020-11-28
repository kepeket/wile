package com.wile.app.di

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Vibrator
import android.view.inputmethod.InputMethodManager
import androidx.preference.PreferenceManager
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
    fun provideInputMethodManager(
        @ApplicationContext context: Context
    ) = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?

    @Provides
    fun provideSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

}
