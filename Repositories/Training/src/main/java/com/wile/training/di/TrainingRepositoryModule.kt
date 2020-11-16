package com.wile.training.di

import com.wile.training.TrainingRepository
import com.wile.training.TrainingRepositoryImpl
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
