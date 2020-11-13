package com.wile.main.persistence.di

import android.app.Application
import androidx.room.Room
import com.squareup.moshi.Moshi
import com.wile.main.persistence.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object PersistenceModule {

    @Provides
    @Singleton
    fun provideMoshi() =  Moshi.Builder().build()

    @Provides
    @Singleton
    fun provideAppDatabase(
        application: Application
    ): AppDatabase = Room
        .databaseBuilder(application, AppDatabase::class.java, "Wile.db")
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun provideTrainingDao(appDatabase: AppDatabase) = appDatabase.trainingDao()

}
