package com.wile.app.ui.main

import androidx.databinding.ObservableBoolean
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.wile.app.base.LiveCoroutinesViewModel
import com.wile.database.model.Training
import com.wile.database.model.TrainingTypes
import com.wile.training.TrainingRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TrainingListingViewModel @ViewModelInject constructor(
    private val trainingRepository: TrainingRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : LiveCoroutinesViewModel() {

    val trainingListLiveData: MutableLiveData<List<Training>> = MutableLiveData()
    val trainingDurationLiveData: MutableLiveData<Int> = MutableLiveData(0)
    var workoutName = MutableLiveData("")

    private val _toastLiveData: MutableLiveData<String> = MutableLiveData()
    val toastLiveData: LiveData<String> get() = _toastLiveData
    val isLoading: ObservableBoolean = ObservableBoolean(false)

    fun fetchTrainings(workout: Int) {
        workoutName.value = "Entrainement #" + (workout + 1)
        viewModelScope.launch {
            isLoading.set(true)
            trainingRepository.fetchTrainingList(
                workout = workout,
                onSuccess = {
                    isLoading.set(false)
                },
                onError = {
                    _toastLiveData.postValue(it)
                }
            ).collect {
                trainingListLiveData.value = it
                trainingDurationLiveData.value = it.map { t -> t.duration }.sum()
            }
        }
    }

    fun deleteTraining(id: Int) {
        viewModelScope.launch {
            trainingRepository.deleteTraining(id)
        }
    }

    fun saveTrainings(trainings: List<Training>) {
        trainings.mapIndexed { i, t ->
            t.sorting = i * 10
        }

        viewModelScope.launch {
            trainingRepository.addAll(trainings)
        }
    }
}