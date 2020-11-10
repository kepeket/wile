package com.wile.main.ui.main

import androidx.annotation.MainThread
import androidx.databinding.ObservableBoolean
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.wile.main.base.LiveCoroutinesViewModel
import com.wile.main.model.Training
import com.wile.main.repository.MainRepository

class MainViewModel @ViewModelInject constructor(
    private val mainRepository: MainRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : LiveCoroutinesViewModel() {

    private var trainingFetchingLiveData: MutableLiveData<Int> = MutableLiveData(0)
    val trainingListLiveData: LiveData<List<Training>>
    val trainingDurationLiveData: LiveData<Int>

    private val _toastLiveData: MutableLiveData<String> = MutableLiveData()
    val toastLiveData: LiveData<String> get() = _toastLiveData
    val isLoading: ObservableBoolean = ObservableBoolean(false)

    init {
        trainingListLiveData = trainingFetchingLiveData.switchMap {
            isLoading.set(true)
            launchOnViewModelScope {
                this.mainRepository.fetchTrainingList(
                        workout = it,
                        onSuccess = {
                            isLoading.set(false)
                        },
                        onError = {
                            _toastLiveData.postValue(it)
                        }
                ).asLiveData()
            }
        }
        trainingDurationLiveData = trainingFetchingLiveData.switchMap {
            launchOnViewModelScope {
                this.mainRepository.fetchTrainingDuration(
                        workout = it,
                        onSuccess = {
                            isLoading.set(false)
                        },
                        onError = {
                            _toastLiveData.postValue(it)
                        }
                ).asLiveData()
            }
        }
    }

    @MainThread
    fun fetchTrainingList(workout: Int) {
        trainingFetchingLiveData.value = workout
    }

    @MainThread
    fun fetchTotalDuration(workout: Int) {
        trainingFetchingLiveData.value = workout
    }
}