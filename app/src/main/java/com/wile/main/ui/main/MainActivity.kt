package com.wile.main.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.wile.main.R
import com.wile.main.base.DataBindingActivity
import com.wile.main.databinding.ActivityMainBinding
import com.wile.main.model.Training
import com.wile.main.ui.adapter.TrainingAdapter
import com.wile.main.ui.add.AddActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.toolbar

@AndroidEntryPoint
class MainActivity : DataBindingActivity(), TrainingAdapter.TouchListenerCallbackInterface {



    val viewModel: MainViewModel by viewModels()
    private val binding: ActivityMainBinding by binding(R.layout.activity_main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        val adapter_ = TrainingAdapter()
        adapter_.setTouchListener(this)
        binding.apply {
            lifecycleOwner = this@MainActivity
            adapter = adapter_
            vm = viewModel
        }
        fab.setOnClickListener {
            val intent = Intent(this, AddActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDeleteTraining(training: Training) {
        viewModel.deleteTraining(training.id)
    }

    override fun onMoveTraining(training: Training) {
        TODO("Not yet implemented")
    }

}