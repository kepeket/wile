package com.wile.reminders.di

import com.wile.reminders.ReminderRepository
import com.wile.reminders.ReminderRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
interface ReminderRepositoryModule {
    @Binds
    @Singleton
    fun provideTrainingRepository(repository: ReminderRepositoryImpl): ReminderRepository
}
