package com.wile.app.sound

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.wile.app.R
import com.wile.sound.Sound
import com.wile.sound.SoundPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class WorkoutSoundPlayer @Inject constructor(
    @ApplicationContext context: Context,
    lifecycleOwner: LifecycleOwner
): SoundPlayer(
    context,
    lifecycleOwner,
    listOf(Beep, Bell, Whistle)
) {
    fun playBeep() = play(Beep)
    fun playWhistle() = play(Whistle)
    fun playBell() = play(Bell)

    private companion object {
        val Beep = Sound(R.raw.beep, 0)
        val Bell = Sound(R.raw.bell, 1)
        val Whistle = Sound(R.raw.whistle, 2)
    }
}
