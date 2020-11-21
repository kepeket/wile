package com.wile.app.di

import android.content.Context
import android.os.Vibrator
import com.google.gson.Gson
import com.squareup.moshi.Moshi
import com.wile.app.ui.social.SocialWorkoutController
import com.wile.app.ui.social.SocialWorkoutUseCase
import com.wile.app.ui.social.WileServer
import com.wile.app.ui.social.WileSocketListener
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import okhttp3.WebSocketListener
import javax.inject.Inject
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
    fun provideGson() = Gson()

    @Provides
    @Singleton
    fun provideMoshi() = Moshi.Builder().build()

    @Provides
    @Singleton
    fun provideOkHttpClient() = OkHttpClient()

    @Provides
    @Singleton
    fun provideWileServer(
        okHttpClient: OkHttpClient,
        gson: Gson
    ) = WileServer(okHttpClient, gson)

    @Provides
    @Singleton
    fun provideSocialWorkoutController(
        server: WileServer
    ) = SocialWorkoutController(server)
}
