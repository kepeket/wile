package com.wile.app.di

import android.content.Context
import android.os.Vibrator
import android.view.inputmethod.InputMethodManager
import com.squareup.moshi.Moshi
import com.wile.app.ui.social.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
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
    @Singleton
    fun provideOkHttpClient() = OkHttpClient()

    @Provides
    @Singleton
    fun provideWileSocketListener(
        moshi: Moshi
    ) = WileSocketListenerImpl(moshi) as WileSocketListener
}
