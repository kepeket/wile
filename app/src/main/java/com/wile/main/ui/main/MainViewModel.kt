package com.wile.main.ui.main

import androidx.annotation.MainThread
import androidx.databinding.ObservableBoolean
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.wile.main.base.LiveCoroutinesViewModel
import com.wile.main.model.Training
import com.wile.main.repository.MainRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainViewModel @ViewModelInject constructor(
    private val mainRepository: MainRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : LiveCoroutinesViewModel() {

    val trainingListLiveData: MutableLiveData<List<Training>> = MutableLiveData()
    val trainingDurationLiveData: MutableLiveData<Int> = MutableLiveData(0)

    private val _toastLiveData: MutableLiveData<String> = MutableLiveData()
    val toastLiveData: LiveData<String> get() = _toastLiveData
    val isLoading: ObservableBoolean = ObservableBoolean(false)

    init {

        viewModelScope.launch {
            isLoading.set(true)
            mainRepository.fetchTrainingList(
                    workout = 0,
                    onSuccess = {
                        isLoading.set(false)
                    },
                    onError = {
                        _toastLiveData.postValue(it)
                    }
            ).collect {
                trainingListLiveData.value = it
            }
        }

        viewModelScope.launch {
            isLoading.set(true)
            mainRepository.fetchTrainingDuration(
                    workout = 0,
                    onSuccess = {
                        isLoading.set(false)
                    },
                    onError = {
                        _toastLiveData.postValue(it)
                    }
            ).collect {
                trainingDurationLiveData.value = it
            }
        }
    }

    fun deleteTraining(id: Int){
        viewModelScope.launch {
            mainRepository.deleteTraining(id)
        }
    }
}