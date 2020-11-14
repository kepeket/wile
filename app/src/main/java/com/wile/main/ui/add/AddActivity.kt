package com.wile.main.ui.add

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.wile.main.R
import com.wile.main.base.DataBindingActivity
import com.wile.main.databinding.ActivityAddBinding
import com.wile.main.model.Training
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_add.*
import kotlinx.android.synthetic.main.item_training.*
import kotlinx.android.synthetic.main.training_name.*
import kotlinx.android.synthetic.main.training_rep_rate.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class AddActivity : DataBindingActivity() {

    private val viewModel: AddViewModel by viewModels()
    private val binding: ActivityAddBinding by binding(R.layout.activity_add)
    private var editMode = false

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
            save_btn.text = getString(R.string.save_training_btn)
        }

        viewModel.fetchTraining(trainingParam)
        save_btn.setOnClickListener {
            validateTraining()
        }

        viewModel.customRepRateToggle.observe(this, {
            binding.trainingRepRateLayout.trainingNameLabel.visibility = if (it) View.VISIBLE else View.GONE
            binding.trainingRepRateLayout.valueLayout.visibility = if (it) View.VISIBLE else View.GONE
        })

        toggle_rep_rate.setOnCheckedChangeListener { _, b ->
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
                    viewModel.saveTraining()
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

        fun newIntent(context: Context) = Intent(context, AddActivity::class.java)
        fun addTraining(context: Context) = newIntent(context)
        fun editTraining(context: Context, trainingId: Int) = newIntent(context).apply {
            putExtra(TRAINING_ID, trainingId)
        }
    }
}