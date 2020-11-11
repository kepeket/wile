package com.wile.main.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Chronometer
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.wile.main.R
import com.wile.main.base.DataBindingActivity
import com.wile.main.databinding.ActivityMainBinding
import com.wile.main.model.Training
import com.wile.main.ui.adapter.TrainingAdapter
import com.wile.main.ui.add.AddActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_sheet.*

@AndroidEntryPoint
class MainActivity : DataBindingActivity(),
    TrainingAdapter.TouchListenerCallbackInterface,
    WorkoutInterface {



    val viewModel: MainViewModel by viewModels()
    private val binding: ActivityMainBinding by binding(R.layout.activity_main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val adapter_ = TrainingAdapter()
        adapter_.setTouchListener(this)
        binding.apply {
            lifecycleOwner = this@MainActivity
            adapter = adapter_
            vm = viewModel
        }
        binding.workoutGo.apply {
            workoutController = this@MainActivity
        }
        setSupportActionBar(binding.mainToolbar.toolbar)
        fab.setOnClickListener {
            val intent = Intent(this, AddActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.training_main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.training_main_go -> {
            startWorkout()
            true
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    fun startWorkout() {
        val sheetBehavior = BottomSheetBehavior.from(binding.workoutGo.trainingGoBottomSheet)
        chronometer.isCountDown = false
        chronometer.base = SystemClock.elapsedRealtime() + 5 * 1000
        chronometer.start()
        chronometerWarmup = true
        chronometerIsRunning = true
        if (sheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    override var chronometerIsRunning: Boolean = false
    override var chronometerWarmup: Boolean = false

    override fun stopWorkout() {
        if (chronometerIsRunning) {
            chronometer.stop()
            chronometerIsRunning = false
        }
        val sheetBehavior = BottomSheetBehavior.from(binding.workoutGo.trainingGoBottomSheet)
        if (sheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    override fun pauseWorkout() {
        if (chronometerIsRunning) {
            chronometer.stop()
            chronometerIsRunning = false
            binding.workoutGo.pause.setImageResource(R.drawable.ic_baseline_play_arrow_24)
        } else {
            chronometer.start()
            chronometerIsRunning = true
            binding.workoutGo.pause.setImageResource(R.drawable.ic_baseline_pause_24)
        }
    }

    override fun skipTraining() {
        chronometer.base = chronometer.base - 15 * 1000
    }

    override fun chronometerTicking(chronometer: Chronometer) {
        val timeDeltaFromStart = ((SystemClock.elapsedRealtime() - chronometer.base) / 1000).toInt()
        if (timeDeltaFromStart == 0) {
            if (chronometerWarmup) {
                chronometerWarmup = false
                viewModel.trainingDurationLiveData.value?.let {
                    chronometer.isCountDown = true
                    chronometer.base = SystemClock.elapsedRealtime() + it * 1000
                }
            }
            else {
                if (chronometerIsRunning) {
                    chronometer.stop()
                }
            }
        }
    }

    override fun onDeleteTraining(training: Training) {
        viewModel.deleteTraining(training.id)
    }

    override fun onMoveTraining(training: Training) {
        TODO("Not yet implemented")
    }

}