package com.wile.main.ui.main

import android.view.View
import android.widget.Chronometer
import androidx.lifecycle.MutableLiveData

interface WorkoutInterface {
    var chronometerIsRunning: Boolean
    var chronometerWarmup: Boolean
    val currentWorkoutLiveData: MutableLiveData<String>

    fun setBottomSheetView(bottomSheet: View)
    fun startWorkout()
    fun stopWorkout()
    fun pauseWorkout()
    fun skipTraining()
    fun chronometerTicking(chronometer: Chronometer)
}