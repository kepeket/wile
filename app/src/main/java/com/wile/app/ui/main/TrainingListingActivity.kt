package com.wile.app.ui.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.content.FileProvider
import androidx.viewpager2.widget.ViewPager2
import com.wile.app.R
import com.wile.app.base.DataBindingActivity
import com.wile.app.databinding.ActivityTrainingListingBinding
import com.wile.app.extensions.showToast
import com.wile.app.ui.adapter.WorkoutListingAdapter
import com.wile.app.ui.add.QuickAddActivity
import com.wile.app.ui.settings.SettingsActivity
import com.wile.app.ui.social.JoinActivity
import com.wile.app.ui.social.SocialWorkoutViewModel
import com.wile.app.ui.workout.WorkoutActivity
import dagger.hilt.android.AndroidEntryPoint
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.Collections

@AndroidEntryPoint
class TrainingListingActivity : DataBindingActivity() {

    private val viewModel: WorkoutListingViewModel by viewModels()
    private val socialViewModel: SocialWorkoutViewModel by viewModels()
    private val binding: ActivityTrainingListingBinding by binding(R.layout.activity_training_listing)
    private val adapter by lazy { WorkoutListingAdapter(this) }
    private var currentWorkout = 0
    private var inRoom = false
    private var isHost = false

    private var imported = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        binding.pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
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

        viewModel.fileExportLiveData.observe(this, {
            if (it.isNotEmpty()) {
                val path: Uri = FileProvider.getUriForFile(
                    this,
                    "com.wile.app.fileprovider",
                    File(it)
                )
                val intent = Intent(Intent.ACTION_SEND).apply {
                    putExtra(Intent.EXTRA_STREAM, path)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    type = "application/json"
                }
                Intent.createChooser(intent, resources.getText(R.string.share_workout_message))
                startActivity(intent)
            }
        })

        intent.data?.let { data ->
            viewModel.workoutListLiveData.observe(this, { list ->
                if (imported) {
                    return@observe
                }
                imported = true
                val maxId = if (list.orEmpty().count() > 0) { Collections.max(list) + 1 } else { 0 }
                val trainingsJSON = contentResolver.openInputStream(data)
                trainingsJSON?.let { json ->
                    val jsonString = BufferedReader(InputStreamReader(json))
                    viewModel.importTrainingsJSON(jsonString.readText(), maxId)
                    showToast(getString(R.string.import_success))
                }
            })

            viewModel.newlyImportedLiveData.observe(this, {
                val lastPos = adapter.getPosition(it)
                binding.pager.post {
                    binding.pager.setCurrentItem(lastPos, true)
                }
            })
        }

        viewModel.toastLiveData.observe(this, {
            showToast(getString(R.string.import_failed))
            finish()
        })

        setSupportActionBar(binding.mainToolbar.toolbar)

        binding.fab.setOnClickListener {
            if (inRoom) {
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
        R.id.training_share_workout -> {
            viewModel.getWorkoutJSON(currentWorkout)
            true
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private const val NEWLY_ADDED_ID = "newly_added_id"

        fun newIntent(context: Context) = Intent(context, TrainingListingActivity::class.java)
        fun showImported(context: Context, newId: Int) = newIntent(context).apply {
            putExtra(NEWLY_ADDED_ID, newId)
        }
    }
}
