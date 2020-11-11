package com.wile.main.ui.handler

import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.os.Build
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.View
import android.widget.Chronometer
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.wile.main.R
import com.wile.main.service.TrainingMediaPlayer
import com.wile.main.ui.main.MainViewModel
import com.wile.main.ui.main.WorkoutInterface
import kotlinx.android.synthetic.main.bottom_sheet.view.*
import java.lang.Math.abs


class WorkoutHandler(val context: Context, val vm: MainViewModel): WorkoutInterface {

    private val mediaplayer by lazy { TrainingMediaPlayer(context) }
    private lateinit var bottomSheetView: View
    private lateinit var chronometer: Chronometer
    private var currentWorkout = 0
    private var totalWorkoutTime = 0
    private var timeLeftBeforeNextTraining = 0

    override var chronometerIsRunning = false
    override var chronometerWarmup = true

    var timeWhenPaused = 0

    override fun setBottomSheetView(bottomSheet: View){
        bottomSheetView = bottomSheet
        chronometer = bottomSheetView.chronometer
    }

    override fun startWorkout(){
        bottomSheetView.let {
            val sheetBehavior = BottomSheetBehavior.from(bottomSheetView)
            if (sheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
            mediaplayer.playCountdown()
            currentWorkout = 0
            chronometer.isCountDown = false
            chronometer.base = SystemClock.elapsedRealtime() + TRAINING_COUNTDOWN_TIME
            chronometerWarmup = true
            chronometerIsRunning = true
            chronometer.start()
        }
    }

    private fun notifyNewTraining() {
        mediaplayer.playWhistle()
        val v = getSystemService(context, VIBRATOR_SERVICE::class.java) as Vibrator?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v?.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            //deprecated in API 26
            v?.vibrate(500)
        }
    }

    private fun changeTraining(workout: Int, skip: Boolean = false){
        vm.trainingListLiveData.value?.let {
            if (skip) {
                chronometer.base -= timeLeftBeforeNextTraining * 1000
            }
            if (workout > 0) {
                notifyNewTraining()
            }
            timeLeftBeforeNextTraining = it[workout].duration
        }
    }

    override fun stopWorkout() {
        if (chronometerIsRunning) {
            chronometer.stop()
            mediaplayer.release()
            chronometerIsRunning = false
        }
        val sheetBehavior = BottomSheetBehavior.from(bottomSheetView)
        if (sheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    override fun pauseWorkout() {
        if (chronometerIsRunning) {
            timeWhenPaused = (chronometer.base - SystemClock.elapsedRealtime()).toInt()
            chronometer.stop()
            mediaplayer.pause()
            bottomSheetView.pause.setImageResource(R.drawable.ic_baseline_play_arrow_24)
        } else {
            chronometer.base = SystemClock.elapsedRealtime() + timeWhenPaused
            chronometer.start()
            mediaplayer.resume()
            bottomSheetView.pause.setImageResource(R.drawable.ic_baseline_pause_24)
        }

        chronometerIsRunning = !chronometerIsRunning
    }

    override fun skipTraining() {
        changeTraining(++currentWorkout, true)
    }

    override fun chronometerTicking(chronometer: Chronometer) {
        val timeDeltaFromStart = ((SystemClock.elapsedRealtime() - chronometer.base) / 1000).toInt()
        Log.i("CRHONO", timeDeltaFromStart.toString())
        Log.i("CRHONO", (totalWorkoutTime - timeDeltaFromStart).toString())
        if (timeDeltaFromStart == 0) {
            if (chronometerWarmup) {
                chronometerWarmup = false
                vm.trainingDurationLiveData.value?.let {
                    totalWorkoutTime = it
                    chronometer.isCountDown = true
                    chronometer.base = SystemClock.elapsedRealtime() + totalWorkoutTime * 1000
                    changeTraining(currentWorkout)
                }
            } else if (chronometerIsRunning) {
                chronometer.stop()
            }
        }
/*        if (!chronometerWarmup) {
            if (timeLeftBeforeNextTraining <= 0) {
                changeTraining(++currentWorkout)
            }
            timeLeftBeforeNextTraining -= totalWorkoutTime - timeDeltaFromStart/1000
        }*/
    }

    private companion object {
        const val TRAINING_COUNTDOWN_TIME = 4 * 1000
    }
}