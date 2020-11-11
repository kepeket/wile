package com.wile.main.ui.main

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.Menu
import android.view.MenuItem
import android.widget.Chronometer
import androidx.activity.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.wile.main.R
import com.wile.main.base.DataBindingActivity
import com.wile.main.databinding.ActivityMainBinding
import com.wile.main.model.Training
import com.wile.main.service.TrainingMediaPlayer
import com.wile.main.ui.adapter.TrainingAdapter
import com.wile.main.ui.add.AddActivity
import com.wile.main.ui.handler.WorkoutHandler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_sheet.*

@AndroidEntryPoint
class MainActivity : DataBindingActivity(),
    TrainingAdapter.TouchListenerCallbackInterface {

    private val viewModel: MainViewModel by viewModels()
    private val binding: ActivityMainBinding by binding(R.layout.activity_main)
    private val adapter by lazy { TrainingAdapter() }
    private val mediaplayer by lazy { TrainingMediaPlayer(this) }
    private val workoutHandler by lazy { WorkoutHandler(this, viewModel) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter.setTouchListener(this)
        binding.apply {
            lifecycleOwner = this@MainActivity
            adapter = this@MainActivity.adapter
            vm = viewModel
        }

        binding.workoutGo.apply {
            workoutController = workoutHandler
        }

        setSupportActionBar(binding.mainToolbar.toolbar)
        fab.setOnClickListener {
            val intent = Intent(this, AddActivity::class.java)
            startActivity(intent)
        }
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

    override fun onDeleteTraining(training: Training) {
        viewModel.deleteTraining(training.id)
    }

    override fun onMoveTraining(training: Training) {
        TODO("Not yet implemented")
    }

}
