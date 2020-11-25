package com.wile.app.ui.handler

import com.wile.database.model.Training
import com.wile.database.model.TrainingTypes

interface WorkoutInterface {
    fun askStartPauseWorkout()
    fun askStopWorkout()
    fun askSkipTraining()
}