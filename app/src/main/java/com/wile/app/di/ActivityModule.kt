package com.wile.app.di

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
class ActivityModule {
        @Provides
        fun provideAppCompatActivity(activity: Activity): AppCompatActivity = activity as AppCompatActivity

        @Provides
        fun provideLifecycleOwner(activity: AppCompatActivity): LifecycleOwner = activity
}
