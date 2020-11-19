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
import java.util.Collections.max

class WorkoutListingViewModel @ViewModelInject constructor(
    private val trainingRepository: TrainingRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : LiveCoroutinesViewModel() {

    val workoutListLiveData: MutableLiveData<List<Int>> = MutableLiveData(listOf(0))

    private val _toastLiveData: MutableLiveData<String> = MutableLiveData()
    val toastLiveData: LiveData<String> get() = _toastLiveData
    val isLoading: ObservableBoolean = ObservableBoolean(false)

    fun fetchWorkouts() {
        viewModelScope.launch {
            isLoading.set(true)
            trainingRepository.fetchWorkoutIds(
                onSuccess = {
                    isLoading.set(false)
                },
                onError = {
                    _toastLiveData.postValue(it)
                }
            ).collect {
                workoutListLiveData.value = it
            }
        }
    }

    fun deleteWorkout(id: Int){
        viewModelScope.launch {
            trainingRepository.deleteTrainings(id)
        }
    }
}