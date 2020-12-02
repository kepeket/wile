package com.wile.app.ui.add

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import com.wile.app.R
import com.wile.core.databinding.DataBindingActivity
import com.wile.app.databinding.ActivityQuickAddBinding
import com.wile.training.model.Preset
import com.wile.database.model.TrainingTypes
import com.wile.app.ui.adapter.TrainingPresetAdapter
import com.wile.features.tabatatraining.TabataAddActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class QuickAddActivity : DataBindingActivity() {

    private val viewModel: QuickAddViewModel by viewModels()
    private val binding: ActivityQuickAddBinding by binding(R.layout.activity_quick_add)
    private val adapter by lazy { TrainingPresetAdapter(
            onTouchPreset = ::onTouchPreset
        )
    }
    private var workoutId: Int = 0

    private fun onTouchPreset(preset: Preset) {
        when(preset.trainingType){
            TrainingTypes.Repeated,
            TrainingTypes.Timed -> {
                runBlocking {
                    launch(Dispatchers.Default) {
                        viewModel.addTrainingFromPreset(preset, workoutId)
                    }
                }
                finish()
            }
            TrainingTypes.Tabata -> {
                startActivityForResult(TabataAddActivity.newTabata(this, workoutId), NEW_CUSTOM_TRAINING)
            }
            TrainingTypes.Custom -> {
                startActivityForResult(AddActivity.newTraining(this, workoutId), NEW_CUSTOM_TRAINING)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.apply {
            adapter = this@QuickAddActivity.adapter
            vm = viewModel
        }

        setSupportActionBar(binding.mainToolbar.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        workoutId = intent.getIntExtra(WORKOUT_ID, 0)
    }


    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        finish()
    }

    companion object {
        const val NEW_CUSTOM_TRAINING = 100
        const val WORKOUT_ID = "workout_id"

        fun newIntent(context: Context, workoutId: Int) = Intent(context, QuickAddActivity::class.java).apply {
            putExtra(WORKOUT_ID, workoutId)
        }
    }
}