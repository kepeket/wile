package com.wile.app.ui.main

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.JsonAdapter
import com.wile.core.viewmodel.LiveCoroutinesViewModel
import com.wile.database.model.Training
import com.wile.training.TrainingRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter

// FixMe : this VM is injected in two places : ImportActivity and TrainingListActivity.
//  Did we want the same instance of the VM in the two activities ? If yes, use by activityViewModels()
class WorkoutListingViewModel @ViewModelInject constructor(
    private val trainingRepository: TrainingRepository,
    @Assisted private val savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context,
    private val trainingListAdapter: JsonAdapter<List<Training>>
) : LiveCoroutinesViewModel() {

    private val _workoutListLiveData: MutableLiveData<List<Int>> = MutableLiveData()
    val workoutListLiveData: LiveData<List<Int>> get() = _workoutListLiveData

    private val _fileExportLiveData: MutableLiveData<String> = MutableLiveData("")
    val fileExportLiveData: LiveData<String> get() = _fileExportLiveData

    private val _newlyImportedLiveData: MutableLiveData<Int> = MutableLiveData()
    val newlyImportedLiveData: LiveData<Int> get() = _newlyImportedLiveData

    private val _toastLiveData: MutableLiveData<String> = MutableLiveData()
    val toastLiveData: LiveData<String> get() = _toastLiveData

    init {
        viewModelScope.launch {
            trainingRepository.fetchWorkoutIds(
                onSuccess = {},
                onError = {
                    _toastLiveData.postValue(it)
                }
            ).collect {
                _workoutListLiveData.value = it
            }
        }
    }

    fun deleteWorkout(id: Int) {
        viewModelScope.launch {
            trainingRepository.deleteTrainings(id)
        }
    }

    fun importTrainingsJSON(json: String, maxId: Int) {
        try {
            val trainings = trainingListAdapter.fromJson(json)
            trainings?.let {
                trainings.forEach {
                    it.id = 0
                    it.workout = maxId
                }

                viewModelScope.launch {
                    trainingRepository.addAll(trainings)
                    _newlyImportedLiveData.postValue(maxId)
                }
            }
        } catch (e: Exception) {
            _toastLiveData.postValue(e.message)
        }
    }

    fun getWorkoutJSON(id: Int) {
        viewModelScope.launch {
            trainingRepository.fetchTrainingList(
                workout = id,
                onSuccess = {},
                onError = {
                    _toastLiveData.postValue(it)
                }
            ).collect {
                // FixMe : you're doing IO operation on the MainThread !
                val dir = File(context.filesDir, DIRECTORY)
                if (!dir.exists()) {
                    dir.mkdir()
                }
                try {
                    val file = File(dir, FILE)
                    val writer = FileWriter(file)
                    val content = trainingListAdapter.toJson(it)
                    writer.append(content)
                    writer.flush()
                    writer.close()
                    _fileExportLiveData.postValue(file.absolutePath)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private companion object {
        const val DIRECTORY = "export"
        const val FILE = "Workout.json"
    }
}
