package com.wile.app.ui.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.wile.app.R
import com.wile.app.base.DataBindingActivity
import com.wile.app.databinding.ActivityTrainingListingBinding
import com.wile.app.ui.adapter.WorkoutListingAdapter
import com.wile.app.ui.add.QuickAddActivity
import com.wile.app.ui.settings.SettingsActivity
import com.wile.app.ui.social.JoinActivity
import com.wile.app.ui.social.SocialWorkoutViewModel
import com.wile.app.ui.workout.WorkoutActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_training_listing.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TrainingListingActivity : DataBindingActivity() {

    private val viewModel: WorkoutListingViewModel by viewModels()
    private val socialViewModel: SocialWorkoutViewModel by viewModels()
    private val binding: ActivityTrainingListingBinding by binding(R.layout.activity_training_listing)
    private val adapter by lazy { WorkoutListingAdapter(this) }
    private var currentWorkout = 0
    private var inRoom = false
    private var isHost = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.fetchWorkouts()

        binding.apply {
            lifecycleOwner = this@TrainingListingActivity
            adapter = this@TrainingListingActivity.adapter
            viewModel = this@TrainingListingActivity.viewModel
            socialViewModel = this@TrainingListingActivity.socialViewModel
        }

        socialViewModel.isInRoom.observe(this, {
            inRoom = it
        })

        socialViewModel.isHost.observe(this, {
            isHost = it
        })

        binding.pager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int){
                super.onPageSelected(position);
                currentWorkout = adapter.getItem(position)
            }
        })

        binding.newTraining.setOnClickListener {
            startActivity(QuickAddActivity.newIntent(this, currentWorkout))
        }

        binding.goSocial.setOnClickListener {
            startActivity(JoinActivity.startWorkout(this, currentWorkout))
        }

        binding.settingsBtn.setOnClickListener {
            startActivity(SettingsActivity.newIntent(this))
        }


        setSupportActionBar(binding.mainToolbar.toolbar)
        fab.setOnClickListener {
            if (inRoom){
                startActivity(WorkoutActivity.startSocialWorkout(this, currentWorkout, isHost))
            } else {
                startActivity(WorkoutActivity.startWorkout(this, currentWorkout))
            }
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
        R.id.training_delete_workout -> {
            adapter.deleteWorkout(currentWorkout)
            viewModel.deleteWorkout(currentWorkout)
            true
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }
}
