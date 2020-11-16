package com.wile.main.sound

import android.content.Context
import android.media.SoundPool

internal fun SoundPool.play(soundId: Int, sound: Sound) {
    play(
        soundId,
        sound.leftVolume,
        sound.rightVolume,
        sound.priority,
        sound.loop,
        sound.rate
    )
}

internal fun SoundPool.loadSounds(context: Context, sounds: List<Sound>): Map<Sound, Int> {
    val loadedSounds = mutableMapOf<Sound, Int>()
    sounds.forEach {
        val soundId = load(context, it.resId, it.priority)
        loadedSounds[it] = soundId
    }
    return loadedSounds
}