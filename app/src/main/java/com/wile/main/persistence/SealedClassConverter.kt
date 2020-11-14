package com.wile.main.persistence

import androidx.room.TypeConverter
import com.wile.main.model.TrainingTypes

class SealedClassConverter {
    @TypeConverter
    fun fromTrainingTypes(value: TrainingTypes): String{
        return when(value){
            TrainingTypes.Timed -> "timed"
            TrainingTypes.Repeated -> "repeated"
            TrainingTypes.Tabata -> "tabata"
            TrainingTypes.Custom -> "custom"
        }
    }

    @TypeConverter
    fun toTrainingTypes(value: String): TrainingTypes{
        return when(value){
            "timed" -> TrainingTypes.Timed
            "repeated" -> TrainingTypes.Repeated
            "tabata" -> TrainingTypes.Tabata
            else -> TrainingTypes.Custom
        }
    }
}