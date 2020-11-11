package com.wile.main.ui.main

import android.widget.Chronometer

interface WorkoutInterface {
    var chronometerIsRunning: Boolean
    var chronometerWarmup: Boolean

    //fun startWorkout()
    fun stopWorkout()
    fun pauseWorkout()
    fun skipTraining()
    fun chronometerTicking(chronometer: Chronometer)
}