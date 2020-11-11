package com.wile.main.service

import android.content.Context
import android.media.MediaPlayer
import com.wile.main.R

class TrainingMediaPlayer(val context: Context) {

    private var released = false
    lateinit var mp: MediaPlayer

    private fun playSound(soundResource: Int){
        mp = MediaPlayer.create(context, soundResource)
        released = false
        mp.setOnCompletionListener {
            it.release()
            released = true
        }
        mp.start()
    }

    fun pause(){
        mp.let {
            if (!released && mp.isPlaying) {
                it.pause()
            }
        }
    }

    fun resume(){
        mp.let {
            if (!released && !mp.isPlaying) {
                it.start()
            }
        }
    }

    fun release(){
        mp.let {
            if (!released) {
                it.stop()
                it.release()
                released = true
            }
        }
    }

    fun playCountdown(){
        playSound(R.raw.countdown)
    }

    fun playWhistle(){
        playSound(R.raw.whistle)
    }

    fun playBell(){
        playSound(R.raw.bell)
    }
}