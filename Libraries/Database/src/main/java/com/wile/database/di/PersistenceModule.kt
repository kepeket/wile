package com.wile.database.di

import android.app.Application
import androidx.room.Room
import com.wile.database.AppDatabase
import com.wile.database.converters.TabataConfigConverter
import com.wile.database.migrations.Schema.Companion.MIGRATION_4_5
import com.wile.database.migrations.Schema.Companion.MIGRATION_5_6
import com.wile.database.migrations.Schema.Companion.MIGRATION_6_7
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
        .addMigrations(MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7)
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun provideTrainingDao(appDatabase: AppDatabase) = appDatabase.trainingDao()

    @Provides
    @Singleton
    fun provideReminderDao(appDatabase: AppDatabase) = appDatabase.reminderDao()

}
