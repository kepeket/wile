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
import java.lang.Math.round
import kotlin.math.roundToInt
import kotlin.math.roundToLong


class WorkoutHandler(val context: Context, val vm: MainViewModel): WorkoutInterface {

    private val mediaplayer by lazy { TrainingMediaPlayer(context) }
    private lateinit var bottomSheetView: View
    private lateinit var chronometer: Chronometer
    private var currentWorkout = 0
    private var totalWorkoutTime = 0
    private var nextTrainingTime = 0L
    private var timeDeltaFromStart = 0
    private var justSkipped = false

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
            currentWorkout = 0
            chronometer.isCountDown = false
            nextTrainingTime = 0
            chronometer.base = SystemClock.elapsedRealtime() + TRAINING_COUNTDOWN_TIME
            chronometerWarmup = true
            chronometerIsRunning = true
            chronometer.start()
        }
    }

    private fun notifyNewTraining() {
        mediaplayer.playWhistle()
        val v = context.getSystemService(VIBRATOR_SERVICE) as Vibrator?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v?.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            //deprecated in API 26
            v?.vibrate(500)
        }
    }

    private fun changeTraining(workout: Int){
        vm.trainingListLiveData.value?.let {
            if (workout > it.count()){
                stopWorkout()
                return
            }
            notifyNewTraining()
            nextTrainingTime = (timeDeltaFromStart + it[workout].duration).toLong()
            Log.i("CHRONO", String.format("new training duration is %d", it[workout].duration ))
        }
    }

    override fun stopWorkout() {
        if (chronometerIsRunning) {
            chronometer.stop()
            mediaplayer.playBell()
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
        if (!chronometerWarmup) {
            chronometer.base -= kotlin.math.abs(timeDeltaFromStart - nextTrainingTime) * 1000
        }
    }

    override fun chronometerTicking(chronometer: Chronometer) {
        timeDeltaFromStart = ((SystemClock.elapsedRealtime() - chronometer.base) / 1000.0).roundToInt()
        val last3sec = kotlin.math.abs(timeDeltaFromStart - nextTrainingTime)
        if (last3sec in 1..4){
            mediaplayer.playBip()
        }
        Log.i("CHRONO", String.format("EOT %d EOW %d", last3sec, timeDeltaFromStart))
        if (timeDeltaFromStart == 0) {
            if (chronometerWarmup) {
                chronometerWarmup = false
                vm.trainingDurationLiveData.value?.let {
                    totalWorkoutTime = it
                    chronometer.isCountDown = true
                    chronometer.base = SystemClock.elapsedRealtime() + (totalWorkoutTime + 1) * 1000
                    changeTraining(currentWorkout)
                }
            } else if (chronometerIsRunning) {
                chronometer.stop()
            }
        } else {
            if (!chronometerWarmup && last3sec == 0L) {
                changeTraining(++currentWorkout)
            }
        }
    }

    private companion object {
        const val TRAINING_COUNTDOWN_TIME = 6 * 1000
    }
}