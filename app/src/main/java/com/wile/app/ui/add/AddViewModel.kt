package com.wile.app.ui.add

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.databinding.ObservableBoolean
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.wile.app.R
import com.wile.app.base.LiveCoroutinesViewModel
import com.wile.database.model.Training
import com.wile.training.TrainingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

@ExperimentalCoroutinesApi
class AddViewModel @ViewModelInject constructor(
    private val trainingRepository: TrainingRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : LiveCoroutinesViewModel()  {

    private val _toastLiveData: MutableLiveData<Any> = MutableLiveData()
    val isLoading: ObservableBoolean = ObservableBoolean(false)
    val customRepRateToggle: MutableLiveData<Boolean> = MutableLiveData(false)
    val toastLiveData: LiveData<Any> get() = _toastLiveData
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
                flow {
                    emit(Training())
                }.flowOn(Dispatchers.IO).asLiveData()
            }
        }
    }

    fun customRepRateChanged(value: Boolean) {
        customRepRateToggle.value= value
        training.value?.customRepRate = value
    }

    @WorkerThread
    suspend fun saveTraining(workout: Int) {
        training.value?.let {t ->
            if (workout > 0) {
                t.workout = workout
            }
            trainingRepository.saveTraining(
                newTraining = t,
                onSuccess = { isLoading.set(false) },
                onError = { _toastLiveData.postValue(it) }
            )
        }
    }

    @MainThread
    fun fetchTraining(id: Int) {
        trainingFetchLiveData.value = id
    }

    fun validateTraining(): Boolean {
        training.value?.let {
            if (it.name.isEmpty()) {
                _toastLiveData.value =  R.string.training_add_missing_name
                return false
            }

            if (it.reps > 0) {
                val repRateUsed = if (it.customRepRate) it.repRate else 30
                it.duration = it.reps * 60 / repRateUsed // rate is on minute
            }
            return true
        }
        return false
    }
}