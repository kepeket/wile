package com.wile.main.di

import android.app.Application
import androidx.room.Room
import com.squareup.moshi.Moshi
import com.wile.main.persistence.AppDatabase
import com.wile.main.persistence.TrainingDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PersistenceModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder().build()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(
        application: Application
    ): AppDatabase {
        return Room
            .databaseBuilder(application, AppDatabase::class.java, "Wile.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideTrainingDao(appDatabase: AppDatabase): TrainingDao {
        return appDatabase.trainingDao()
    }

}