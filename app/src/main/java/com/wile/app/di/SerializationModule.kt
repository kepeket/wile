package com.wile.app.di

import com.google.gson.Gson
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.wile.app.model.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object SerializationModule {
    // FixMe : Gson or Moshi, choose you side. You are using 2 different parsing solutions.
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
}
