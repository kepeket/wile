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
import androidx.lifecycle.MutableLiveData
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.wile.main.R
import com.wile.main.model.Training
import com.wile.main.service.TrainingMediaPlayer
import com.wile.main.ui.main.MainViewModel
import com.wile.main.ui.main.WorkoutInterface
import kotlinx.android.synthetic.main.bottom_sheet.view.*
import kotlin.math.roundToInt


class WorkoutHandler(val context: Context, val vm: MainViewModel): WorkoutInterface {

    private val mediaplayer by lazy { TrainingMediaPlayer(context) }
    private lateinit var bottomSheetView: View
    private lateinit var chronometer: Chronometer
    private var currentWorkout = 0
    private var totalWorkoutTime = 0
    private var nextTrainingTime = 0L
    private val trainingList: MutableList<Training> = mutableListOf()

    override var chronometerIsRunning = false
    override var chronometerWarmup = true

    override val currentWorkoutLiveData = MutableLiveData<String>()

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
            vm.trainingListLiveData.value?.let {
                if (it.count() > 0){
                    trainingList.clear()
                    trainingList.addAll(it)
                    val warmup = Training(
                            duration = 5,
                            name = "Mise en place",
                            sorting = 0
                    )
                    trainingList.add(0, warmup)
                    currentWorkout = 0
                    chronometer.isCountDown = true
                    chronometerIsRunning = true
                    setChronometerBase()
                    updateInfoDisplay()
                    chronometer.start()
                }
            }
        }
    }

    private fun updateInfoDisplay(){
        if (currentWorkout < trainingList.count()) {
            val train =  trainingList[currentWorkout]
            var info = ""
            val minutes: Int = train.duration / (60)
            val seconds: Int = train.duration % 60
            if (train.reps != 0){
                info = String.format("%d %s (~%d:%02d)", train.reps, train.name, minutes, seconds)
            } else {
                info = String.format("%s (%d:%02d)",  train.name, minutes, seconds)
            }
            currentWorkoutLiveData.value = info
        }
    }

    private fun setChronometerBase() {
        totalWorkoutTime = trainingList.subList(currentWorkout, trainingList.lastIndex + 1).map { t -> t.duration }.sum() + 1
        nextTrainingTime = SystemClock.elapsedRealtime() + trainingList[currentWorkout].duration.toLong() * 1000
        Log.i("CHRONO", String.format("new training duration is %d", trainingList[currentWorkout].duration))
        chronometer.base = SystemClock.elapsedRealtime() + totalWorkoutTime  * 1000
    }

    private fun notifyNewTraining() {
        updateInfoDisplay()
        mediaplayer.playWhistle()
        val v = context.getSystemService(VIBRATOR_SERVICE) as Vibrator?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v?.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            //deprecated in API 26
            v?.vibrate(500)
        }
    }

    private fun changeTraining(){
        trainingList.let {
            currentWorkout++
            notifyNewTraining()
            nextTrainingTime = SystemClock.elapsedRealtime() + trainingList[currentWorkout].duration.toLong() * 1000
        }
    }

    override fun stopWorkout() {
        if (chronometerIsRunning) {
            chronometer.stop()
            chronometer.base = SystemClock.elapsedRealtime()
            chronometerIsRunning = false
        }
        mediaplayer.playBell()
        val sheetBehavior = BottomSheetBehavior.from(bottomSheetView)
        if (sheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    override fun pauseWorkout() {
        if (chronometerIsRunning) {
            timeWhenPaused = (chronometer.base - SystemClock.elapsedRealtime()).toInt()
            chronometer.stop()
            bottomSheetView.pause.setImageResource(R.drawable.ic_baseline_play_arrow_24)
        } else {
            chronometer.base = SystemClock.elapsedRealtime() + timeWhenPaused
            chronometer.start()
            bottomSheetView.pause.setImageResource(R.drawable.ic_baseline_pause_24)
        }

        chronometerIsRunning = !chronometerIsRunning
    }

    override fun skipTraining() {
        if (currentWorkout < trainingList.count() - 1) {
            currentWorkout++
            setChronometerBase()
            notifyNewTraining()
        }
        else {
            stopWorkout()
        }
    }

    override fun chronometerTicking(chronometer: Chronometer) {
        val endOfWorkout = ((SystemClock.elapsedRealtime() - chronometer.base) / 1000.0).roundToInt()
        val last3sec = kotlin.math.abs((SystemClock.elapsedRealtime() - nextTrainingTime) / 1000.0).roundToInt()
        if (last3sec in 1..4) {
            mediaplayer.playBip()
        }
        Log.i("CHRONO", String.format("EOT %d (%d) EOW %d", nextTrainingTime, last3sec, endOfWorkout))
        if (endOfWorkout == 0) {
            stopWorkout()
        } else {
            if (last3sec == 0) {
                changeTraining()
            }
        }
    }
}