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

    private var trainingFetchingLiveData: MutableLiveData<Int> = MutableLiveData(0)
    val trainingListLiveData: MutableLiveData<List<Training>> = MutableLiveData()
    val trainingDurationLiveData: LiveData<Int>

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

    fun deleteTraining(id: Int){
        viewModelScope.launch {
            mainRepository.deleteTraining(id)
        }
    }
}