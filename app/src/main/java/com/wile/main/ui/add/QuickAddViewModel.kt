package com.wile.main.ui.add

import androidx.annotation.WorkerThread
import androidx.databinding.ObservableBoolean
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.wile.main.base.LiveCoroutinesViewModel
import com.wile.main.model.Preset
import com.wile.main.model.TrainingTypes
import com.wile.main.repositories.TrainingRepository

class QuickAddViewModel @ViewModelInject constructor(
    private val trainingRepository: TrainingRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : LiveCoroutinesViewModel()  {

    private val _toastLiveData: MutableLiveData<Any> = MutableLiveData()
    val isLoading: ObservableBoolean = ObservableBoolean(false)
    val toastLiveData: LiveData<Any> get() = _toastLiveData
    val presets: MutableLiveData<List<Preset>> = MutableLiveData()

    init {
        presets.value = getPresets()
    }

    // FixMe : this is typicaly something that comes from a UC and not from a VM (Business related)
    private fun getPresets(): List<Preset> {
        return listOf(
            // Rest
            Preset(name = "Repos", trainingType = TrainingTypes.Timed, duration = 20),
            // Custom
            Preset(name = "Sur mesure", trainingType = TrainingTypes.Custom),
            // Tabata
            Preset(name = "Tabata", trainingType = TrainingTypes.Tabata),
            // Repeated
            Preset(name = "Pompes", trainingType = TrainingTypes.Repeated, reps = 20),
            Preset(name = "Dips", trainingType = TrainingTypes.Repeated, reps = 20),
            Preset(name = "Crunch", trainingType = TrainingTypes.Repeated),
            Preset(name = "Fentes", trainingType = TrainingTypes.Repeated),
            Preset(name = "Squats", trainingType = TrainingTypes.Repeated),
            Preset(name = "Burpees", trainingType = TrainingTypes.Repeated, reps = 20),
            Preset(name = "Russian twist", trainingType = TrainingTypes.Repeated),
            // Timed
            Preset(name = "Jumping jacks", trainingType = TrainingTypes.Timed),
            Preset(name = "Montée de genoux", trainingType = TrainingTypes.Timed),
            Preset(name = "Gainage", trainingType = TrainingTypes.Timed, duration = 60),
            Preset(name = "Mountain climbers", trainingType = TrainingTypes.Timed),
            Preset(name = "Talons/fesses", trainingType = TrainingTypes.Timed),
            Preset(name = "Corde à sauter", trainingType = TrainingTypes.Timed, duration = 60),
            Preset(name = "Shadow boxing", trainingType = TrainingTypes.Timed, duration = 45),
        )
    }

    /*
     FixMe : in a "strict" application of Clean Architecture, VM should not have a reference
      to Repository, but to UC that have reference to Repository
     */
    @WorkerThread
    suspend fun addTrainingFromPreset(preset: Preset){
        trainingRepository.addTrainingFromPreset(preset)
    }
}