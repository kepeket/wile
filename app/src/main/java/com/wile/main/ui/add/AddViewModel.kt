package com.wile.main.ui.add

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.databinding.ObservableBoolean
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.squareup.inject.assisted.Assisted
import com.wile.main.base.LiveCoroutinesViewModel
import com.wile.main.model.Training
import com.wile.main.repository.AddRepository
import com.wile.main.repository.MainRepository

class AddViewModel @ViewModelInject constructor(
    private val addRepository: AddRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : LiveCoroutinesViewModel() {

    private val _toastLiveData: MutableLiveData<String> = MutableLiveData()
    val isLoading: ObservableBoolean = ObservableBoolean(false)
    val toastLiveData: LiveData<String> get() = _toastLiveData

    @WorkerThread
    suspend fun saveTraining(newTraining_: Training){
        addRepository.saveTraining(
            newTraining = newTraining_,
            onSuccess = { isLoading.set(false) },
            onError = { _toastLiveData.postValue(it) }
        )
    }
}