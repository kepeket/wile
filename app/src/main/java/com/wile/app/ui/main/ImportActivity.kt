package com.wile.app.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import com.wile.app.R
import com.wile.app.base.DataBindingActivity
import com.wile.app.extensions.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ImportActivity : DataBindingActivity() {

    private val viewModel: WorkoutListingViewModel by viewModels()
    private var imported = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.fetchWorkouts()

        viewModel.workoutListLiveData.observe   (this, { list ->
            if (imported) {
                return@observe
            }
            imported = true
            val maxId = Collections.max(list) + 1
            val workoutUri = intent.data
            workoutUri?.let { uri ->
                val trainingsJSON = contentResolver.openInputStream(uri)
                trainingsJSON?.let { json ->
                    val jsonString = BufferedReader(InputStreamReader(json))
                    viewModel.importTrainingsJSON(jsonString.readText(), maxId)
                    showToast(getString(R.string.import_success))
                    startActivity(TrainingListingActivity.showImported(this, maxId))
                }
            }
            finish()
        })

        viewModel.toastLiveData.observe(this, {
            showToast(getString(R.string.import_failed))
            finish()
        })
    }
}