package com.wile.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import dagger.hilt.android.qualifiers.ApplicationContext

abstract class SoundPlayer constructor(
    @ApplicationContext private val context: Context,
    lifecycleOwner: LifecycleOwner,
    private val sounds: List<Sound>,
    // Empirical values
    private val maxStreams: Int = 3,
    private val contentType: Int = AudioAttributes.CONTENT_TYPE_SONIFICATION,
    private val usage: Int = AudioAttributes.USAGE_MEDIA
): LifecycleObserver {
    private var soundPool: SoundPool? = null
    private lateinit var loadedSounds: Map<Sound, Int>

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    protected fun play(sound: Sound) {
        soundPool?.play(
            loadedSounds.getValue(sound),
            sound
        )
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun createSoundPool() {
        soundPool = SoundPool.Builder()
            .setMaxStreams(maxStreams)
            .setAudioAttributes(AudioAttributes.Builder()
                .setContentType(contentType)
                .setUsage(usage)
                .build()
            )
            .build().apply {
                loadedSounds = loadSounds(context, sounds)
            }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroySoundPool() {
        soundPool?.release()
        soundPool = null
    }
}
