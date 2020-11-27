package com.wile.app.di

import com.squareup.moshi.Moshi
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.lifecycle.LifecycleRegistry
import com.tinder.scarlet.messageadapter.moshi.MoshiMessageAdapter
import com.tinder.scarlet.streamadapter.rxjava2.RxJava2StreamAdapterFactory
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import com.wile.app.ui.social.WileServer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build()

    @Provides
    @Singleton
    fun provideLifecycleRegistry() = LifecycleRegistry(0L)

    @Provides
    @Singleton
    fun provideScarlett(
        okHttpClient: OkHttpClient,
        lifecycleRegistry: LifecycleRegistry,
        moshi: Moshi
    ) = Scarlet.Builder()
            .webSocketFactory(okHttpClient.newWebSocketFactory(WileServer.SERVER_URL))
            .lifecycle(lifecycleRegistry)
            .addMessageAdapterFactory(MoshiMessageAdapter.Factory(moshi))
            .addStreamAdapterFactory(RxJava2StreamAdapterFactory())
            .build().create<WileServer>()
}
