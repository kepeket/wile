package com.wile.app.ui.workout

import android.app.Service
import android.content.Intent
import android.os.*
import com.wile.app.ui.handler.WorkoutInterface
import com.wile.database.model.Training
import com.wile.database.model.TrainingTypes
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.workout_controller.view.*
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
    private var chronometerIsRunning = false
    private lateinit var timer: Timer

    private var workoutListener: WorkoutInterface? = null

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
        chronometerIsRunning = true
        skipTraining()
        workoutListener?.workoutStarted()
        timer = fixedRateTimer("timer", false, 0L, 1000) {
            chronometerTicking()
        }

    }

    fun stopWorkout() {
        if (chronometerIsRunning) {
            timer.cancel()
            chronometerIsRunning = false
            currentTraining = -1
        }
        workoutListener?.workoutStopped()
    }

    private fun pauseWorkout() {
        if (chronometerIsRunning) {
            timeWhenPaused = SystemClock.elapsedRealtime()
        } else {
            lastTrainingChange = SystemClock.elapsedRealtime() - (timeWhenPaused - lastTrainingChange)
        }
        chronometerIsRunning = !chronometerIsRunning
        workoutListener?.workoutPaused(chronometerIsRunning)
    }

    private fun chronometerTicking() {
        val elapsedTime = if (chronometerIsRunning) {
            ((SystemClock.elapsedRealtime() - lastTrainingChange) / 1000.0).roundToInt()
        } else {
            ((timeWhenPaused - lastTrainingChange) / 1000.0).roundToInt()
        }
        val endOfTraining = trainingCountdown - elapsedTime
        workoutListener?.chronometerTicked(
                elapsedTime,
                endOfTraining,
                expendedTrainings[currentTraining].duration,
                expendedTrainings[currentTraining].trainingType
        )
        if (endOfTraining <= 0) {
            if (expendedTrainings[currentTraining].trainingType != TrainingTypes.Repeated) {
                skipTraining()
            }
        }
    }

    fun skipTraining() {
        var notify = false
        if (currentTraining < 0 && !chronometerIsRunning) {
            return
        }
        currentTraining++
        if (currentTraining > 0 && currentTraining <= expendedTrainings.count() - 1) {
            notify = true
        }
        if (currentTraining <= expendedTrainings.count() - 1) {
            workoutListener?.trainingSkipped(notify,
                    expendedTrainings[currentTraining].duration,
                    currentTraining,
                    expendedTrainings[currentTraining])
            trainingCountdown = expendedTrainings[currentTraining].duration
            lastTrainingChange = SystemClock.elapsedRealtime()
        } else {
            stopWorkout()
        }
    }

    fun setTrainingList(trainings: List<Training>) {
        expendedTrainings = trainings
    }

    fun setListener(listener: WorkoutInterface) {
        workoutListener = listener
    }
}