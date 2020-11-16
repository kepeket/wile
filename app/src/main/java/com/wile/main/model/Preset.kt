package com.wile.main.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.wile.database.model.TRAINING_DEFAULT_DURATION
import com.wile.database.model.TRAINING_DEFAULT_REP_COUNT
import com.wile.database.model.TRAINING_DEFAULT_REP_RATE
import com.wile.database.model.TrainingTypes

data class Preset(
    val name:String,
    val reps: Int = TRAINING_DEFAULT_REP_COUNT,
    val repRate: Int = TRAINING_DEFAULT_REP_RATE,
    val duration: Int = TRAINING_DEFAULT_DURATION,
    val trainingType: TrainingTypes
) : BaseObservable() {
    @Bindable
    fun getDescription(): String{
        return when(trainingType){
            TrainingTypes.Timed -> "$duration secondes"
            TrainingTypes.Repeated -> "$reps repetitions"
            TrainingTypes.Tabata -> "8 cycles de deux exercices HIIT"
            TrainingTypes.Custom -> "rajouter votre excercice sur mesure"
        }
    }
}

