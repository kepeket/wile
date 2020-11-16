package com.wile.main.repositories.di

import com.wile.main.repositories.TrainingRepository
import com.wile.main.repositories.TrainingRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
interface TrainingRepositoryModule {
    @Binds
    @Singleton
    fun provideTrainingRepository(repository: TrainingRepositoryImpl): TrainingRepository
}
