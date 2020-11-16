package com.wile.app.ui.add

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import com.wile.app.R
import com.wile.app.base.DataBindingActivity
import com.wile.app.databinding.ActivityTabataAddBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_add.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Integer.parseInt

@AndroidEntryPoint
class TabataAddActivity : DataBindingActivity() {

    private val viewModel: TabataAddViewModel by viewModels()
    private val binding: ActivityTabataAddBinding by binding(R.layout.activity_tabata_add)
    private var editMode: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.apply {
            lifecycleOwner = this@TabataAddActivity
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
                    viewModel.saveTraining()
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

        fun newIntent(context: Context) = Intent(context, TabataAddActivity::class.java)
        fun editTabata(context: Context, trainingId: Int) = newIntent(context).apply {
            putExtra(TRAINING_ID, trainingId)
        }
    }
}