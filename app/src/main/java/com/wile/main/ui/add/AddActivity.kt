package com.wile.main.ui.add

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import com.wile.main.R
import com.wile.main.base.DataBindingActivity
import com.wile.main.databinding.ActivityAddBinding
import com.wile.main.model.Training
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_add.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


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
        setSupportActionBar(binding.mainToolbar.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        save_btn.setOnClickListener {
            validateTraining()
        }
    }

    private fun validateTraining() {
        if (training_title.text.isNullOrEmpty()){
            Toast.makeText(this, getString(R.string.training_add_missing_name), Toast.LENGTH_SHORT).show()
            return
        }
        var computedDuration = 30
        if (training_duration.text.isNotBlank()){
            computedDuration = training_duration.text.toString().toInt()
        }
        var reps = 0
        var repRate = 0
        if (training_reps.text.isNotBlank()){
            reps = training_reps.text.toString().toInt()
        }
        if (training_rep_rate.text.isNotBlank()){
            repRate = training_rep_rate.text.toString().toInt()
        }
        if (reps != 0) {
            var defaultRate = 30
            if (repRate != 0) {
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
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.training_add_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.training_main_go -> {
            validateTraining()
            true
        }
        android.R.id.home -> {
            onBackPressed()
            true;
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }
}