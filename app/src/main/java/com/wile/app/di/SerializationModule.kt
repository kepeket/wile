package com.wile.app.di

import com.google.gson.Gson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.wile.app.model.*
import com.wile.database.model.Training
import dagger.Module
import dagger.Provides
import dagger.Reusable
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
        .add(EnvelopTypeAdapter())
        .add(WorkoutActionAdapter())
        .add(TrainingTypesAdapter())
        .add(
            PolymorphicJsonAdapterFactory.of(Envelop::class.java, "type")
                .withSubtype(EnvelopRoom::class.java, ENVELOP_TYPE_ROOM)
                .withSubtype(EnvelopPing::class.java, ENVELOP_TYPE_PING)
                .withSubtype(EnvelopPong::class.java, ENVELOP_TYPE_PONG)
                .withSubtype(EnvelopError::class.java, ENVELOP_TYPE_ERROR)
                .withSubtype(EnvelopWorkout::class.java, ENVELOP_TYPE_WORKOUT)
        )
        .add(
            PolymorphicJsonAdapterFactory.of(RoomMessageAction::class.java, "action")
                .withSubtype(RoomMessageAction.Joined::class.java, ROOM_ACTION_JOINED)
                .withSubtype(RoomMessageAction.Created::class.java, ROOM_ACTION_CREATED)
                .withSubtype(RoomMessageAction.Left::class.java, ROOM_ACTION_LEFT)
        )
        .add(
            PolymorphicJsonAdapterFactory.of(WorkoutMessageAction::class.java, "action")
                .withSubtype(WorkoutMessageAction.Started::class.java, WORKOUT_ACTION_STARTED)
                .withSubtype(WorkoutMessageAction.Paused::class.java, WORKOUT_ACTION_PAUSED)
                .withSubtype(WorkoutMessageAction.Stopped::class.java, WORKOUT_ACTION_STOPPED)
                .withSubtype(WorkoutMessageAction.Tick::class.java, WORKOUT_ACTION_TICK)
                .withSubtype(WorkoutMessageAction.TrainingStart::class.java, WORKOUT_ACTION_TRAINING_START)
        )
        .add(KotlinJsonAdapterFactory())
        .build()

    @Provides
    @Reusable
    fun provideEnvelopAdapter(moshi: Moshi): JsonAdapter<Envelop>
            = moshi.adapter(Envelop::class.java)

    @Provides
    @Reusable
    fun provideListTrainingAdapter(moshi: Moshi): JsonAdapter<List<Training>>
            = moshi.adapter(Types.newParameterizedType(
        List::class.java,
        Training::class.java
    ))
}
