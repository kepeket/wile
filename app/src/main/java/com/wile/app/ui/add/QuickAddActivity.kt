package com.wile.app.ui.add

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import com.wile.app.R
import com.wile.app.base.DataBindingActivity
import com.wile.app.databinding.ActivityQuickAddBinding
import com.wile.training.model.Preset
import com.wile.database.model.TrainingTypes
import com.wile.app.ui.adapter.TrainingPresetAdapter
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

    private fun onTouchPreset(preset: com.wile.training.model.Preset) {
        when(preset.trainingType){
            TrainingTypes.Repeated,
            TrainingTypes.Timed -> {
                runBlocking {
                    launch(Dispatchers.Default) {
                        viewModel.addTrainingFromPreset(preset)
                    }
                }
                finish()
            }
            TrainingTypes.Tabata -> {
                startActivityForResult(TabataAddActivity.newIntent(this), NEW_CUSTOM_TRAINING)
            }
            TrainingTypes.Custom -> {
                startActivityForResult(AddActivity.newIntent(this), NEW_CUSTOM_TRAINING)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.apply {
            lifecycleOwner = this@QuickAddActivity
            adapter = this@QuickAddActivity.adapter
            vm = viewModel
        }

        setSupportActionBar(binding.mainToolbar.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

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

        fun newIntent(context: Context) = Intent(context, QuickAddActivity::class.java)
    }
}