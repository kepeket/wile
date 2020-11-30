package com.wile.app.ui.add

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import com.wile.app.R
import com.wile.core.databinding.DataBindingActivity
import com.wile.app.databinding.ActivityAddBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class AddActivity : DataBindingActivity() {

    private val viewModel: AddViewModel by viewModels()
    private val binding: ActivityAddBinding by binding(R.layout.activity_add)
    private var editMode = false
    private var workoutId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.apply {
            lifecycleOwner = this@AddActivity
            vm = viewModel
        }

        setSupportActionBar(binding.mainToolbar.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val trainingParam = intent.getIntExtra(TRAINING_ID, -1)
        if (trainingParam > 0){
            editMode = true
            supportActionBar?.title = getString(R.string.edit_training_toolbar_title)
            binding.saveBtn.text = getString(R.string.save_training_btn)
        }

        workoutId = intent.getIntExtra(WORKOUT_ID, -1)
        viewModel.fetchTraining(trainingParam)
        binding.saveBtn.setOnClickListener {
            validateTraining()
        }

        viewModel.customRepRateToggle.observe(this, {
            binding.trainingRepRateLayout.trainingNameLabel.visibility = if (it) View.VISIBLE else View.GONE
            binding.trainingRepRateLayout.valueLayout.visibility = if (it) View.VISIBLE else View.GONE
        })

        binding.trainingRepRateLayout.toggleRepRate.setOnCheckedChangeListener { _, b ->
            viewModel.customRepRateChanged(b)
        }
    }

    /* FixMe : you should use startActivityForResult in the calling class to get a result
        (did the user has effectively add something or not ?) and so call setResult here
    */
    private fun validateTraining() {
        val ok = viewModel.validateTraining()

        if (ok) {
            runBlocking {
                launch(Dispatchers.Default) {
                    viewModel.saveTraining(workoutId)
                }
            }

            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.training_add_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.training_main_go -> {
            validateTraining()
            true
        }
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
        const val TRAINING_COUNT = "training_count"
        const val TAG = "AddActivith"
        const val WORKOUT_ID = "workout_id"

        fun newIntent(context: Context) = Intent(context, AddActivity::class.java)
        fun newTraining(context: Context, workoutId: Int) = newIntent(context).apply {
            putExtra(WORKOUT_ID, workoutId)
        }
        fun editTraining(context: Context, trainingId: Int) = newIntent(context).apply {
            putExtra(TRAINING_ID, trainingId)
        }
    }
}