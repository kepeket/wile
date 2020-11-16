package com.wile.database.di

import android.app.Application
import androidx.room.Room
import com.google.gson.Gson
import com.wile.database.AppDatabase
import com.wile.database.converters.TabataConfigConverter
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
    fun provideAppDatabase(
        application: Application,
        tabataConfigConverter: TabataConfigConverter,
    ): AppDatabase = Room
        .databaseBuilder(application, AppDatabase::class.java, "Wile.db")
        .addTypeConverter(tabataConfigConverter)
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun provideTrainingDao(appDatabase: AppDatabase) = appDatabase.trainingDao()

    @Provides
    @Singleton
    fun provideTabataConfigConverter(gson: Gson) = TabataConfigConverter(gson)
}
