package com.wile.app.ui.main

import android.content.Context
import androidx.databinding.ObservableBoolean
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.wile.app.base.LiveCoroutinesViewModel
import com.wile.database.model.Training
import com.wile.training.TrainingRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter
import java.lang.reflect.Type

class WorkoutListingViewModel @ViewModelInject constructor(
    private val trainingRepository: TrainingRepository,
    @Assisted private val savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context,
    private val moshi: Moshi
) : LiveCoroutinesViewModel() {

    val workoutListLiveData: MutableLiveData<List<Int>> = MutableLiveData()
    val fileExportLiveData: MutableLiveData<String> = MutableLiveData("")
    val newlyImportedLiveData: MutableLiveData<Int> = MutableLiveData()

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

    fun deleteWorkout(id: Int) {
        viewModelScope.launch {
            trainingRepository.deleteTrainings(id)
        }
    }

    fun importTrainingsJSON(json: String, maxId: Int) {
        val type: Type = Types.newParameterizedType(
            List::class.java,
            Training::class.java
        )
        val adapter: JsonAdapter<List<Training>> = moshi.adapter(type)
        try {
            val trainings = adapter.fromJson(json)
            trainings?.let {
                for (t in trainings) {
                    t.id = 0
                    t.workout = maxId
                }
                viewModelScope.launch {
                    trainingRepository.addAll(trainings)
                    newlyImportedLiveData.postValue(maxId)
                }
            }
        } catch (e: Exception) {
            _toastLiveData.postValue(e.message)
        }
    }

    fun getWorkoutJSON(id: Int) {
        viewModelScope.launch {
            isLoading.set(true)
            trainingRepository.fetchTrainingList(
                workout = id,
                onSuccess = {
                    isLoading.set(false)
                },
                onError = {
                    _toastLiveData.postValue(it)
                }
            ).collect {
                // FixMe : you're doing IO operation on the MainThread !
                val dir = File(context.filesDir, "export")
                if (!dir.exists()) {
                    dir.mkdir()
                }
                try {
                    val file = File(dir, "Workout.json")
                    val writer = FileWriter(file)
                    val type: Type = Types.newParameterizedType(
                        List::class.java,
                        Training::class.java
                    )
                    val adapter: JsonAdapter<List<Training>> = moshi.adapter(type)
                    val content = adapter.toJson(it)
                    writer.append(content)
                    writer.flush()
                    writer.close()
                    fileExportLiveData.postValue(file.absolutePath)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}