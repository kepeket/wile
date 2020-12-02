package com.wile.features.tabatatraining

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import com.wile.core.databinding.DataBindingActivity
import com.wile.features.tabatatraining.databinding.ActivityTabataAddBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Integer.parseInt

@AndroidEntryPoint
class TabataAddActivity : DataBindingActivity() {

    private val viewModel: TabataAddViewModel by viewModels()
    private val binding: ActivityTabataAddBinding by binding(R.layout.activity_tabata_add)
    private var workoutId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.apply {
            lifecycleOwner = this@TabataAddActivity
            vm = viewModel
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val trainingParam = intent.getIntExtra(TRAINING_ID, -1)
        if (trainingParam > 0){
            supportActionBar?.title = getString(R.string.edit_training_toolbar_title)
            binding.saveBtn.text = getString(R.string.save_training_btn)
        }
        workoutId = intent.getIntExtra(WORKOUT_ID, -1)
        viewModel.fetchTraining(trainingParam)

        binding.tabataMainName.trainingNameLabel.text = getString(R.string.tabata_form_main_label)
        binding.tabataAlterName.trainingNameLabel.text = getString(R.string.tabata_form_alter_label)
        binding.tabataCycles.trainingNameLabel.text = getString(R.string.tabata_form_cycles_label)


        binding.tabataMainName.trainingTitle.addTextChangedListener {
            viewModel.updateMainName(it.toString())
        }
        binding.tabataMainDuration.trainingDuration.addTextChangedListener {
            viewModel.updateMainDuration(parseInt(it.toString()))
        }

        binding.tabataAlterName.trainingTitle.addTextChangedListener {
            viewModel.updateAlterName(it.toString())
        }
        binding.tabataAlterDuration.trainingDuration.addTextChangedListener {
            viewModel.updateAlterDuration(parseInt(it.toString()))
        }
        binding.tabataCycles.trainingDuration.addTextChangedListener {
            viewModel.updateCycles(parseInt(it.toString()))
        }

        binding.saveBtn.setOnClickListener {
            saveTraining()
        }
    }

    private fun saveTraining(){
        if (viewModel.validateTraining()) {
            runBlocking {
                launch(Dispatchers.Default) {
                    viewModel.saveTraining(workoutId)
                }
            }
            finish()
        }
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

    companion object {
        const val TRAINING_ID = "training_id"
        const val WORKOUT_ID = "workout_id"

        fun newIntent(context: Context) = Intent(context, TabataAddActivity::class.java)
        fun newTabata(context: Context, workoutId: Int) = newIntent(context).apply {
            putExtra(WORKOUT_ID, workoutId)
        }
        fun editTabata(context: Context, trainingId: Int) = newIntent(context).apply {
            putExtra(TRAINING_ID, trainingId)
        }
    }
}