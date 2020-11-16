package com.wile.app.persistence

import androidx.room.TypeConverter
import com.wile.app.model.TrainingTypes

class SealedClassConverter {
    @TypeConverter
    fun fromTrainingTypes(value: TrainingTypes): String{
        return when(value){
            TrainingTypes.Timed -> TIMED
            TrainingTypes.Repeated -> REPEATED
            TrainingTypes.Tabata -> TABATA
            TrainingTypes.Custom -> CUSTOM
        }
    }

    @TypeConverter
    fun toTrainingTypes(value: String): TrainingTypes{
        return when(value){
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