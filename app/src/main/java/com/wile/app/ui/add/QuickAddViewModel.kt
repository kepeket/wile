package com.wile.app.ui.add

import androidx.annotation.WorkerThread
import androidx.databinding.ObservableBoolean
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.wile.app.base.LiveCoroutinesViewModel
import com.wile.training.model.Preset
import com.wile.database.model.TrainingTypes
import com.wile.training.TrainingRepository

class QuickAddViewModel @ViewModelInject constructor(
    private val trainingRepository: TrainingRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : LiveCoroutinesViewModel()  {

    private val _toastLiveData: MutableLiveData<Any> = MutableLiveData()
    val isLoading: ObservableBoolean = ObservableBoolean(false)
    val toastLiveData: LiveData<Any> get() = _toastLiveData
    val presets: MutableLiveData<List<com.wile.training.model.Preset>> = MutableLiveData()

    init {
        presets.value = getPresets()
    }

    // FixMe : this is typicaly something that comes from a UC and not from a VM (Business related)
    private fun getPresets(): List<com.wile.training.model.Preset> {
        return listOf(
            // Rest
            com.wile.training.model.Preset(
                name = "Repos",
                trainingType = TrainingTypes.Timed,
                duration = 20
            ),
            // Custom
            com.wile.training.model.Preset(
                name = "Sur mesure",
                trainingType = TrainingTypes.Custom
            ),
            // Tabata
            com.wile.training.model.Preset(name = "Tabata", trainingType = TrainingTypes.Tabata),
            // Repeated
            com.wile.training.model.Preset(
                name = "Pompes",
                trainingType = TrainingTypes.Repeated,
                reps = 20
            ),
            com.wile.training.model.Preset(
                name = "Dips",
                trainingType = TrainingTypes.Repeated,
                reps = 20
            ),
            com.wile.training.model.Preset(name = "Crunch", trainingType = TrainingTypes.Repeated),
            com.wile.training.model.Preset(name = "Fentes", trainingType = TrainingTypes.Repeated),
            com.wile.training.model.Preset(name = "Squats", trainingType = TrainingTypes.Repeated),
            com.wile.training.model.Preset(
                name = "Burpees",
                trainingType = TrainingTypes.Repeated,
                reps = 20
            ),
            com.wile.training.model.Preset(
                name = "Russian twist",
                trainingType = TrainingTypes.Repeated
            ),
            // Timed
            com.wile.training.model.Preset(
                name = "Jumping jacks",
                trainingType = TrainingTypes.Timed
            ),
            com.wile.training.model.Preset(
                name = "Montée de genoux",
                trainingType = TrainingTypes.Timed
            ),
            com.wile.training.model.Preset(
                name = "Gainage",
                trainingType = TrainingTypes.Timed,
                duration = 60
            ),
            com.wile.training.model.Preset(
                name = "Mountain climbers",
                trainingType = TrainingTypes.Timed
            ),
            com.wile.training.model.Preset(
                name = "Talons/fesses",
                trainingType = TrainingTypes.Timed
            ),
            com.wile.training.model.Preset(
                name = "Corde à sauter",
                trainingType = TrainingTypes.Timed,
                duration = 60
            ),
            com.wile.training.model.Preset(
                name = "Shadow boxing",
                trainingType = TrainingTypes.Timed,
                duration = 45
            ),
        )
    }

    /*
     FixMe : in a "strict" application of Clean Architecture, VM should not have a reference
      to Repository, but to UC that have reference to Repository
     */
    @WorkerThread
    suspend fun addTrainingFromPreset(preset: com.wile.training.model.Preset){
        trainingRepository.addTrainingFromPreset(preset)
    }
}