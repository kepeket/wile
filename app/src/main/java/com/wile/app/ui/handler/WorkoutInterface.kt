package com.wile.app.ui.handler

import com.wile.database.model.Training
import com.wile.database.model.TrainingTypes

interface WorkoutInterface {
    fun askStartPauseWorkout()
    fun askStopWorkout()
    fun askSkipTraining()
    fun workoutStarted()
    fun workoutStopped()
    fun workoutPaused(isPaused: Boolean)
    fun trainingSkipped(needNotify: Boolean, trainingDuration: Int, progress: Int, newTraining: Training)
    fun chronometerTicked(elapsedTime: Int, countdown: Int, trainingDuration: Int, trainingType: TrainingTypes)
}