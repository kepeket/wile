package com.wile.app.ui.main

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.wile.app.base.LiveCoroutinesViewModel
import com.wile.database.model.Training
import com.wile.training.TrainingRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TrainingListingViewModel @ViewModelInject constructor(
    private val trainingRepository: TrainingRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : LiveCoroutinesViewModel() {

    private val _trainingListLiveData: MutableLiveData<List<Training>> = MutableLiveData()
    val trainingListLiveData: LiveData<List<Training>> get() = _trainingListLiveData

    private val _trainingDurationLiveData: MutableLiveData<Int> = MutableLiveData(0)
    val trainingDurationLiveData: LiveData<Int> get() = _trainingDurationLiveData

    private val _workoutName: MutableLiveData<String> = MutableLiveData("")
    val workoutName: LiveData<String> get () = _workoutName

    private val _toastLiveData: MutableLiveData<String> = MutableLiveData()
    val toastLiveData: LiveData<String> get() = _toastLiveData

    fun fetchTrainings(workout: Int) {
        _workoutName.value = "Entrainement #" + (workout + 1)

        viewModelScope.launch {
            trainingRepository.fetchTrainingList(
                workout = workout,
                onSuccess = {},
                onError = { _toastLiveData.postValue(it) }
            ).collect {
                _trainingListLiveData.value = it
                _trainingDurationLiveData.value = it.map { t -> t.duration }.sum()
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
