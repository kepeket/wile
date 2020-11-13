package com.wile.main.service

import android.content.Context
import android.media.MediaPlayer
import com.wile.main.R

class TrainingMediaPlayer(val context: Context) {

    private var released = false
    lateinit var mp: MediaPlayer

    private val beepMedia = MediaPlayer.create(context, R.raw.beep)
    private val whistleMedia = MediaPlayer.create(context, R.raw.whistle)
    private val bellMedia = MediaPlayer.create(context, R.raw.bell)

    fun playBip(){
        beepMedia.start()
    }

    fun playWhistle(){
        whistleMedia.start()
    }

    fun playBell(){
        bellMedia.start()
    }
}