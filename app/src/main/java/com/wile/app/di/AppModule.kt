package com.wile.app.di

import android.content.Context
import android.os.Vibrator
import androidx.hilt.Assisted
import androidx.lifecycle.SavedStateHandle
import com.google.gson.Gson
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.wile.app.model.*
import com.wile.app.ui.social.*
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
    fun provideMoshi() = Moshi.Builder()
        .add(RoomActionAdapter())
        .add(
            PolymorphicJsonAdapterFactory.of(Envelop::class.java, "type")
                .withSubtype(EnvelopRoom::class.java, ENVELOP_TYPE_ROOM)
                .withSubtype(EnvelopPing::class.java, ENVELOP_TYPE_PING)
                .withSubtype(EnvelopPong::class.java, ENVELOP_TYPE_PONG)
                .withSubtype(EnvelopError::class.java, ENVELOP_TYPE_ERROR)
        )
        .add(
            PolymorphicJsonAdapterFactory.of(RoomMessageAction::class.java, "action")
                .withSubtype(RoomMessageAction.Joined::class.java, ROOM_ACTION_JOINED)
                .withSubtype(RoomMessageAction.Created::class.java, ROOM_ACTION_CREATED)
                .withSubtype(RoomMessageAction.Left::class.java, ROOM_ACTION_LEFT)
        )
        .add(KotlinJsonAdapterFactory())
        .build()

    @Provides
    @Singleton
    fun provideOkHttpClient() = OkHttpClient()


    @Provides
    @Singleton
    fun provideWileServer(
        okHttpClient: OkHttpClient,
        moshi: Moshi
    ) = WileServer(okHttpClient, moshi)

    @Provides
    @Singleton
    fun provideSocialWorkoutController(
        server: WileServer
    ) = SocialWorkoutController(server)

    @Provides
    @Singleton
    fun provideWileSocketListener(
        moshi: Moshi
    ) = WileSocketListenerImpl(moshi) as WileSocketListener

    @Provides
    @Singleton
    fun provideSocialWorkoutUseCase(
        listener: WileSocketListener,
        workoutController: SocialWorkoutController
    ) = SocialWorkoutUseCase(listener, workoutController)

}
