package com.wile.main.model

import androidx.databinding.BaseObservable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Training(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var workout: Int = 0,
    var name: String = "",
    var reps: Int = 0,
    var repRate: Int = 30,
    var duration: Int = 30,

    var sorting: Int = 1000,
    var customRepRate: Boolean = false,
    var trainingType: TrainingTypes = TrainingTypes.Custom
) : BaseObservable()

sealed class TrainingTypes {
    object Timed : TrainingTypes()
    object Repeated: TrainingTypes()
    object Tabata: TrainingTypes()
    object Custom: TrainingTypes()
}

const val TRAINING_DEFAULT_REP_RATE = 30
const val TRAINING_DEFAULT_REP_COUNT = 40
const val TRAINING_DEFAULT_DURATION = 30
const val TRAINING_DEFAULT_TABATA_1_DURATION = 20
const val TRAINING_DEFAULT_TABATA_2_DURATION = 10
const val TRAINING_DEFAULT_TABATA_REPEAT = 8