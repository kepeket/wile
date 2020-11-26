package com.wile.app.model

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonClass
import com.squareup.moshi.ToJson
import com.wile.database.model.Training

class WorkoutModels {
    @JsonClass(generateAdapter = true)
    data class WorkoutMessage(
        val userId: String,
        val name: String,
        val action: WorkoutMessageAction,
        val training: Training,
        val timestamp: Long,
        val countdown: Int
    ) : WileMessage
}

sealed class WorkoutMessageAction {
    object Start: WorkoutMessageAction()
    object Started: WorkoutMessageAction()
    object Stop: WorkoutMessageAction()
    object Stopped: WorkoutMessageAction()
    object Lobby: WorkoutMessageAction()
    object Ready: WorkoutMessageAction()
    object TrainingStart: WorkoutMessageAction()
}

const val WORKOUT_ACTION_STARTED = "started"
const val WORKOUT_ACTION_START = "start"
const val WORKOUT_ACTION_LOBBY = "lobby"
const val WORKOUT_ACTION_READY = "ready"
const val WORKOUT_ACTION_TRAINING_START = "new_training"
const val WORKOUT_ACTION_STOP = "stop"
const val WORKOUT_ACTION_STOPPED = "stopped"


class WorkoutActionAdapter {
    @FromJson
    fun fromJson(action: String): WorkoutMessageAction = when (action) {
        WORKOUT_ACTION_STARTED -> WorkoutMessageAction.Started
        WORKOUT_ACTION_START -> WorkoutMessageAction.Start
        WORKOUT_ACTION_LOBBY -> WorkoutMessageAction.Lobby
        WORKOUT_ACTION_READY -> WorkoutMessageAction.Ready
        WORKOUT_ACTION_TRAINING_START -> WorkoutMessageAction.TrainingStart
        WORKOUT_ACTION_STOP -> WorkoutMessageAction.Stop
        WORKOUT_ACTION_STOPPED -> WorkoutMessageAction.Stopped
        else -> throw RuntimeException("Not support data type")
    }

    @ToJson
    fun toJson(workoutAction: WorkoutMessageAction): String = when (workoutAction) {
        WorkoutMessageAction.Start -> WORKOUT_ACTION_START
        WorkoutMessageAction.Started -> WORKOUT_ACTION_STARTED
        WorkoutMessageAction.Stop -> WORKOUT_ACTION_STOP
        WorkoutMessageAction.Stopped -> WORKOUT_ACTION_STOPPED
        WorkoutMessageAction.Lobby -> WORKOUT_ACTION_LOBBY
        WorkoutMessageAction.Ready -> WORKOUT_ACTION_READY
        WorkoutMessageAction.TrainingStart -> WORKOUT_ACTION_TRAINING_START
    }
}