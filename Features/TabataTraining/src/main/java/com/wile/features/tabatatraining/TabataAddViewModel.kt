package com.wile.features.tabatatraining

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.databinding.ObservableBoolean
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.wile.core.viewmodel.LiveCoroutinesViewModel
import com.wile.database.model.TabataConfig
import com.wile.database.model.Training
import com.wile.database.model.TrainingTypes
import com.wile.training.TrainingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class TabataAddViewModel @ViewModelInject constructor(
    private val trainingRepository: TrainingRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : LiveCoroutinesViewModel()  {

    private val _toastLiveData: MutableLiveData<Any> = MutableLiveData()
    val isLoading: ObservableBoolean = ObservableBoolean(false)
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
                    val training = Training(name = "Tabata",
                        tabataConfig = TabataConfig(),
                        trainingType = TrainingTypes.Tabata)
                    emit(training)
                }.flowOn(Dispatchers.IO).asLiveData()
            }
        }
    }

    @WorkerThread
    suspend fun saveTraining(workoutId: Int){
        training.value?.let {t ->
            t.tabataConfig?.let {
                t.duration = it.cycles * (it.mainDuration + it.alterDuration)
            }

            if (workoutId > 0) {
                t.workout = workoutId
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

    fun updateMainName(name: String) {
        training.value?.tabataConfig?.mainName = name
    }

    fun updateAlterName(name: String) {
        training.value?.tabataConfig?.alterName = name
    }

    fun updateMainDuration(duration: Int) {
        training.value?.tabataConfig?.mainDuration = duration
    }

    fun updateAlterDuration(duration: Int) {
        training.value?.tabataConfig?.alterDuration = duration
    }

    fun updateCycles(count: Int) {
        training.value?.tabataConfig?.cycles = count
    }

    fun validateTraining(): Boolean {
        training.value?.let {
            if (it.name.isEmpty()) {
                _toastLiveData.value =  R.string.training_add_missing_name
                return false
            }

            it.tabataConfig?.let { (_, mainDuration, alterDuration, mainName) ->
                if (mainName.isEmpty()) {
                    _toastLiveData.value =  R.string.training_tabata_missing_main
                    return false
                }

                if (mainName.isEmpty()) {
                    _toastLiveData.value =  R.string.training_tabata_missing_alter
                    return false
                }

                if (mainDuration <= 0) {
                    _toastLiveData.value =  R.string.training_tabata_missing_duration
                    return false
                }

                if (alterDuration <= 0) {
                    _toastLiveData.value =  R.string.training_tabata_missing_duration
                    return false
                }
                return true
            } ?: run {
                _toastLiveData.value = R.string.training_tabata_error
            }
        }
        return false
    }
}