package com.wile.app.ui.handler

import android.widget.Chronometer

interface WorkoutInterface {
    var chronometerIsRunning: Boolean

    fun startStopWorkout()
    fun startWorkout()
    fun stopWorkout()
    fun pauseWorkout()
    fun skipTraining()
    fun chronometerTicking(chronometer: Chronometer)
}