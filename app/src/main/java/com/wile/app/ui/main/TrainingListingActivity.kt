package com.wile.app.ui.main

import android.os.Bundle
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Chronometer
import androidx.activity.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.wile.app.R
import com.wile.app.base.DataBindingActivity
import com.wile.app.databinding.ActivityTrainingListingBinding
import com.wile.database.model.Training
import com.wile.database.model.TrainingTypes
import com.wile.app.sound.WorkoutSoundPlayer
import com.wile.app.ui.adapter.WorkoutAdapter
import com.wile.app.ui.add.QuickAddActivity
import com.wile.app.ui.handler.WorkoutInterface
import com.wile.app.ui.workout.WorkoutActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_training_listing.*
import kotlinx.android.synthetic.main.bottom_sheet.view.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject
import kotlin.math.roundToInt

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TrainingListingActivity : DataBindingActivity() {

    private val viewModel: WorkoutListingViewModel by viewModels()
    private val binding: ActivityTrainingListingBinding by binding(R.layout.activity_training_listing)
    private val adapter by lazy { WorkoutAdapter(this) }
    private var currentWorkout = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.fetchWorkouts()

        binding.apply {
            lifecycleOwner = this@TrainingListingActivity
            adapter = this@TrainingListingActivity.adapter
            viewModel = this@TrainingListingActivity.viewModel
        }

        binding.pager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int){
                super.onPageSelected(position);
                currentWorkout = position
            }
        })

        binding.newTraining.setOnClickListener {
            startActivity(QuickAddActivity.newIntent(this, currentWorkout))
        }

        setSupportActionBar(binding.mainToolbar.toolbar)
        fab.setOnClickListener {
            startActivity(WorkoutActivity.startWorkout(this, currentWorkout))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.training_main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.training_main_go -> {
            val lastPos = adapter.addWorkout()
            binding.pager.post {
                binding.pager.setCurrentItem(lastPos, true)
            }
            true
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }
}
