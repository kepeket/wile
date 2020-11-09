package com.wile.main.ui.add

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import com.wile.main.R
import com.wile.main.base.DataBindingActivity
import com.wile.main.databinding.ActivityAddBinding
import com.wile.main.databinding.ActivityMainBinding
import com.wile.main.model.Training
import com.wile.main.ui.adapter.TrainingAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_add.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.math.ceil

@AndroidEntryPoint
class AddActivity : DataBindingActivity() {

    val viewModel: AddViewModel by viewModels()
    private val binding: ActivityAddBinding by binding(R.layout.activity_add)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.apply {
            lifecycleOwner = this@AddActivity
            vm = viewModel
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.save_training -> {
            var computedDuration = training_duration.text.toString().toInt()
            val reps = training_reps.text.toString().toInt()
            val repRate = training_rep_rate.text.toString().toInt()
            if (reps != 0){
                var defaultRate = 30
                if (repRate != 0){
                    defaultRate = repRate
                }
                computedDuration = reps * 60 / defaultRate // rate is on minute
            }
            val training = Training(
                name = training_title.text.toString(),
                repRate = repRate,
                reps = reps,
                duration = computedDuration,
                sorting = 10
            )
            runBlocking {
                launch(Dispatchers.Default) {
                    viewModel.saveTraining(training)
                }
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