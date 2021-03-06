package com.wile.app.ui.main

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.wile.core.viewmodel.LiveCoroutinesViewModel
import com.wile.database.model.Training
import com.wile.training.TrainingRepository
import com.wile.app.extensions.duration
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TrainingListingViewModel @ViewModelInject constructor(
    private val trainingRepository: TrainingRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : LiveCoroutinesViewModel() {

    private val _emptyListLiveData: MutableLiveData<Boolean> = MutableLiveData(false)
    val emptyListLiveData: LiveData<Boolean> get() = _emptyListLiveData

    private val _trainingListLiveData: MutableLiveData<List<Training>> = MutableLiveData()
    val trainingListLiveData: LiveData<List<Training>> get() = _trainingListLiveData

    private val _trainingDurationLiveData: MutableLiveData<Int> = MutableLiveData(0)
    val trainingDurationLiveData: LiveData<Int> get() = _trainingDurationLiveData

    private val _toastLiveData: MutableLiveData<String> = MutableLiveData()
    val toastLiveData: LiveData<String> get() = _toastLiveData

    private val workoutId: Int = requireNotNull(savedStateHandle.get<Int>(TrainingListingFragment.WORKOUT_ID))
    private val _workoutName: MutableLiveData<Int> = MutableLiveData(workoutId)
    val workoutName: LiveData<Int> get () = _workoutName

    init {
        viewModelScope.launch {
            trainingRepository.fetchTrainingList(
                workout = workoutId,
                onSuccess = {},
                onError = { _toastLiveData.postValue(it) }
            ).collect {
                _trainingListLiveData.value = it
                _emptyListLiveData.value = it.isEmpty()
                _trainingDurationLiveData.value = it.duration()
            }
        }
    }

    fun deleteTraining(id: Int) {
        viewModelScope.launch {
            trainingRepository.deleteTraining(id)
        }
    }

    fun saveTrainings(trainings: List<Training>) {
        trainings.mapIndexed { index, training ->
            training.sorting = index * 10
        }

        viewModelScope.launch {
            trainingRepository.addAll(trainings)
        }
    }
}
