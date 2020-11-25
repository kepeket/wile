package com.wile.app.ui.workout

import android.app.Service
import android.content.Intent
import android.os.*
import androidx.lifecycle.MutableLiveData
import com.wile.database.model.Training
import com.wile.database.model.TrainingTypes
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.workout_controller.view.*
import timber.log.Timber
import java.util.*
import kotlin.concurrent.fixedRateTimer
import kotlin.math.roundToInt

@AndroidEntryPoint
class WorkoutService: Service() {

    private val binder = WorkoutBinder()

    private lateinit var expendedTrainings: List<Training>
    private var currentTraining = -1
    private var trainingCountdown = 0
    private var lastTrainingChange = 0L
    private var timeWhenPaused = 0L
    private var timeWhenStarted = 0L
    private var chronometerIsRunning = false
    private lateinit var timer: Timer

    val countdownLiveData: MutableLiveData<Int> = MutableLiveData(-1)
    val elapsedTimeLiveData: MutableLiveData<Int> = MutableLiveData(0)
    val workoutProgressLiveData: MutableLiveData<Int> = MutableLiveData(currentTraining)
    val currentTrainingLiveData: MutableLiveData<Training> = MutableLiveData()
    val chronometerIsRunningLiveData: MutableLiveData<Boolean> = MutableLiveData(chronometerIsRunning)
    val workoutIsDoneLiveData: MutableLiveData<Boolean> = MutableLiveData(false)

    override fun onBind(p0: Intent?): IBinder? {
        return binder
    }

    inner class WorkoutBinder : Binder() {
        fun getService(): WorkoutService = this@WorkoutService
    }

    // Private region
    fun startStopWorkout(): Boolean {
        if (expendedTrainings.count() > 1) {
            if (currentTraining < 0) {
                startWorkout()
            } else {
                pauseWorkout()
            }
            return true
        }
        return false
    }

    private fun startWorkout() {
        currentTraining = -1
        workoutProgressLiveData.postValue(currentTraining)
        chronometerIsRunning = true
        skipTraining()
        chronometerIsRunningLiveData.postValue(chronometerIsRunning)
        timeWhenStarted = SystemClock.elapsedRealtime()
        timer = fixedRateTimer("timer", false, 0L, 1000) {
            chronometerTicking()
        }

    }

    fun stopWorkout() {
        if (chronometerIsRunning) {
            timer.cancel()
            chronometerIsRunning = false
            chronometerIsRunningLiveData.postValue(chronometerIsRunning)
            currentTraining = -1
            workoutProgressLiveData.postValue(currentTraining)
        }
        workoutIsDoneLiveData.postValue(true)
    }

    private fun pauseWorkout() {
        if (chronometerIsRunning) {
            timeWhenPaused = SystemClock.elapsedRealtime()
        } else {
            lastTrainingChange = SystemClock.elapsedRealtime() - (timeWhenPaused - lastTrainingChange)
        }
        chronometerIsRunning = !chronometerIsRunning
        chronometerIsRunningLiveData.postValue(chronometerIsRunning)
    }

    private fun chronometerTicking() {
        val elapsedTime = if (chronometerIsRunning) {
            ((SystemClock.elapsedRealtime() - lastTrainingChange) / 1000.0).roundToInt()
        } else {
            ((timeWhenPaused - lastTrainingChange) / 1000.0).roundToInt()
        }
        val endOfTraining = trainingCountdown - elapsedTime
        if (endOfTraining <0 ){
            Timber.d("OKAY")
        }
        elapsedTimeLiveData.postValue(((SystemClock.elapsedRealtime() - timeWhenStarted)/1000.0).roundToInt())
        countdownLiveData.postValue(endOfTraining)
        if (endOfTraining <= 0) {
            if (expendedTrainings[currentTraining].trainingType != TrainingTypes.Repeated) {
                skipTraining()
            }
        }
    }

    fun skipTraining() {
        if (currentTraining < 0 && !chronometerIsRunning) {
            return
        }
        currentTraining++
        workoutProgressLiveData.postValue(currentTraining)
        currentTrainingLiveData.postValue(expendedTrainings[currentTraining])
        countdownLiveData.postValue(expendedTrainings[currentTraining].duration)
        if (currentTraining <= expendedTrainings.count() - 1) {
            trainingCountdown = expendedTrainings[currentTraining].duration
            lastTrainingChange = SystemClock.elapsedRealtime()
        } else {
            stopWorkout()
        }
    }

    fun setTrainingList(trainings: List<Training>) {
        expendedTrainings = trainings
    }
}