package com.wile.app.repositories.di

import com.wile.app.repositories.TrainingRepository
import com.wile.app.repositories.TrainingRepositoryImpl
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
