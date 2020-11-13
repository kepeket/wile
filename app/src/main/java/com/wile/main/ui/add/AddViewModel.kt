package com.wile.main.ui.add

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.databinding.ObservableBoolean
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.wile.main.base.LiveCoroutinesViewModel
import com.wile.main.model.Training
import com.wile.main.repositories.TrainingRepository

class AddViewModel @ViewModelInject constructor(
    private val trainingRepository: TrainingRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : LiveCoroutinesViewModel() {

    private val _toastLiveData: MutableLiveData<String> = MutableLiveData()
    val isLoading: ObservableBoolean = ObservableBoolean(false)
    val toastLiveData: LiveData<String> get() = _toastLiveData
    var training: LiveData<Training>
    val trainingFetchLiveData: MutableLiveData<Int> = MutableLiveData(-1)

    init {
        training = trainingFetchLiveData.switchMap { id ->
            if (id > 0) {
                launchOnViewModelScope {
                    trainingRepository.getTraining(
                            id = id,
                            onSuccess = { isLoading.set(false) },
                            onError = { _toastLiveData.postValue(it) }
                    ).asLiveData()
                }
            } else {
                liveData {
                    Training(name = "", duration = 30, sorting = 10)
                }
            }
        }
    }

    @WorkerThread
    suspend fun saveTraining(newTraining_: Training){
        trainingRepository.saveTraining(
            newTraining = newTraining_,
            onSuccess = { isLoading.set(false) },
            onError = { _toastLiveData.postValue(it) }
        )
    }

    @MainThread
    fun fetchTraining(id: Int){
        trainingFetchLiveData.value = id
    }
}