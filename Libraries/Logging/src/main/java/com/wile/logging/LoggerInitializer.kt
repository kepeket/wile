package com.wile.logging

import android.content.Context
import androidx.startup.Initializer
import timber.log.Timber

class LoggerInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        return
    }

    // No dependencies
    override fun dependencies() = emptyList<Class<out Initializer<*>>>()
}