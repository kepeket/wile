package com.wile.app.di

import com.squareup.moshi.JsonAdapter
import com.wile.app.model.Envelop
import com.wile.app.ui.social.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideOkHttpClient() = OkHttpClient()

    @Provides
    @Singleton
    fun provideWileSocketListener(
            wileSocketListener: WileSocketListener
    ) = wileSocketListener

    @Provides
    @Singleton
    fun provideWileSocketListener(
        envelopAdapter: JsonAdapter<Envelop>
    ): WileSocketListener = WileSocketListenerImpl(envelopAdapter)
}
