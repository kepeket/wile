package com.wile.app.model

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonClass
import com.squareup.moshi.ToJson
import com.wile.database.model.TrainingTypes

class WorkoutModels {
    @JsonClass(generateAdapter = true)
    data class WorkoutMessage(
        val userId: String,
        val name: String,
        val action: WorkoutMessageAction,
        val training: TrainingLight,
        val timestamp: Long,
        val countdown: Int,
        val trainingCount: Int,
        val trainingPos: Int
    ) : WileMessage
}

data class TrainingLight(
    var name: String = "",
    var reps: Int = 0,
    var repRate: Int = 30,
    var trainingType: TrainingTypes = TrainingTypes.Custom,
)

sealed class WorkoutMessageAction {
    object Start : WorkoutMessageAction()
    object Started : WorkoutMessageAction()
    object Pause : WorkoutMessageAction()
    object Paused : WorkoutMessageAction()
    object Stop : WorkoutMessageAction()
    object Stopped : WorkoutMessageAction()
    object Lobby : WorkoutMessageAction()
    object Ready : WorkoutMessageAction()
    object TrainingStart : WorkoutMessageAction()
    object Tick : WorkoutMessageAction()
}

const val WORKOUT_ACTION_STARTED = "started"
const val WORKOUT_ACTION_START = "start"
const val WORKOUT_ACTION_PAUSE = "pause"
const val WORKOUT_ACTION_PAUSED = "paused"
const val WORKOUT_ACTION_LOBBY = "lobby"
const val WORKOUT_ACTION_READY = "ready"
const val WORKOUT_ACTION_TRAINING_START = "new_training"
const val WORKOUT_ACTION_STOP = "stop"
const val WORKOUT_ACTION_STOPPED = "stopped"
const val WORKOUT_ACTION_TICK = "tick"

class WorkoutActionAdapter {
    @FromJson
    fun fromJson(action: String): WorkoutMessageAction = when (action) {
        WORKOUT_ACTION_STARTED -> WorkoutMessageAction.Started
        WORKOUT_ACTION_START -> WorkoutMessageAction.Start
        WORKOUT_ACTION_PAUSE -> WorkoutMessageAction.Pause
        WORKOUT_ACTION_PAUSED -> WorkoutMessageAction.Paused
        WORKOUT_ACTION_LOBBY -> WorkoutMessageAction.Lobby
        WORKOUT_ACTION_READY -> WorkoutMessageAction.Ready
        WORKOUT_ACTION_TRAINING_START -> WorkoutMessageAction.TrainingStart
        WORKOUT_ACTION_STOP -> WorkoutMessageAction.Stop
        WORKOUT_ACTION_STOPPED -> WorkoutMessageAction.Stopped
        WORKOUT_ACTION_TICK -> WorkoutMessageAction.Tick
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
        WorkoutMessageAction.Pause -> WORKOUT_ACTION_PAUSE
        WorkoutMessageAction.Paused -> WORKOUT_ACTION_PAUSED
        WorkoutMessageAction.Tick -> WORKOUT_ACTION_TICK
    }
}

class TrainingTypesAdapter {
    @ToJson
    fun toJson(value: TrainingTypes): String {
        return when (value) {
            TrainingTypes.Timed -> TIMED
            TrainingTypes.Repeated -> REPEATED
            TrainingTypes.Tabata -> TABATA
            TrainingTypes.Custom -> CUSTOM
        }
    }

    @FromJson
    fun fromJson(value: String): TrainingTypes {
        return when (value) {
            TIMED -> TrainingTypes.Timed
            REPEATED -> TrainingTypes.Repeated
            TABATA -> TrainingTypes.Tabata
            else -> TrainingTypes.Custom
        }
    }

    // Fixed keys to avoid mismatch
    private companion object {
        const val TIMED = "timed"
        const val REPEATED = "repeated"
        const val TABATA = "tabata"
        const val CUSTOM = "custom"
    }
}