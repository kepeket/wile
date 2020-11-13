package com.wile.main.ui.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import com.wile.main.R
import com.wile.main.base.DataBindingActivity
import com.wile.main.databinding.ActivityTrainingListingBinding
import com.wile.main.model.Training
import com.wile.main.ui.adapter.TrainingAdapter
import com.wile.main.ui.add.AddActivity
import com.wile.main.ui.handler.WorkoutHandler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_training_listing.*
import javax.inject.Inject

@AndroidEntryPoint
class TrainingListingActivity : DataBindingActivity() {

    private val viewModel: TrainingListingViewModel by viewModels()
    private val binding: ActivityTrainingListingBinding by binding(R.layout.activity_training_listing)
    private val adapter by lazy { TrainingAdapter(
        onDeleteTraining = ::onDeleteTraining,
        onMoveTraining =  ::onMoveTraining,
        onTouchTraining = ::onTouchTraining
    )}

    @Inject lateinit var workoutHandler : WorkoutHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // FixMe : temporary
        workoutHandler.viewModel = viewModel

        binding.apply {
            lifecycleOwner = this@TrainingListingActivity
            adapter = this@TrainingListingActivity.adapter
            viewModel = this@TrainingListingActivity.viewModel
        }

        binding.workoutGo.apply {
            workoutController = workoutHandler
        }

        setSupportActionBar(binding.mainToolbar.toolbar)
        fab.setOnClickListener { startActivity(AddActivity.newIntent(this)) }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.training_main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.training_main_go -> {
            workoutHandler.startWorkout()
            true
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    private fun onDeleteTraining(training: Training) {
        viewModel.deleteTraining(training.id)
    }

    private fun onMoveTraining(trainings: List<Training>) {
        viewModel.saveTrainings(trainings)
    }

    private fun onTouchTraining(training: Training) {
        startActivity(AddActivity.newIntent(this, training.id))
    }
}
