package com.wile.main.ui.main

import androidx.databinding.ObservableBoolean
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.wile.main.base.LiveCoroutinesViewModel
import com.wile.main.model.Training
import com.wile.main.model.TrainingTypes
import com.wile.main.repositories.TrainingRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.math.exp

class TrainingListingViewModel @ViewModelInject constructor(
    private val trainingRepository: TrainingRepository,
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
            trainingRepository.fetchTrainingList(
                    workout = 0,
                    onSuccess = {
                        isLoading.set(false)
                    },
                    onError = {
                        _toastLiveData.postValue(it)
                    }
            ).collect {
                trainingListLiveData.value = it
                trainingDurationLiveData.value = it.map { t -> t.duration }.sum()
            }
        }
    }

    fun deleteTraining(id: Int){
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

    fun getExpendedTrainingList(): List<Training> {
        val expended: MutableList<Training> = mutableListOf()
        trainingListLiveData.value?.let { list ->
            expended.add(
                Training(name = "Préparation", duration = 15, trainingType = TrainingTypes.Timed, sorting = 0)
            )
            for (training in list){
                when(training.trainingType){
                    TrainingTypes.Tabata -> {
                        training.tabataConfig?.let { tabataConfig ->
                            for (loop in 0 until tabataConfig.cycles){
                                val mainTraining = Training(
                                    name = String.format("Tabata / %s", tabataConfig.mainName),
                                    duration = tabataConfig.mainDuration,
                                    trainingType = TrainingTypes.Timed
                                )
                                val alterTraining = Training(
                                    name = String.format("Tabata / %s", tabataConfig.alterName),
                                    duration = tabataConfig.alterDuration,
                                    trainingType = TrainingTypes.Timed
                                )
                                with(expended){
                                    add(mainTraining)
                                    add(alterTraining)
                                }
                            }
                        }
                    }
                    else -> {
                        expended.add(training)
                    }
                }
            }
        }
        return expended
    }
}