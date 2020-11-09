package com.wile.main.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.wile.main.R
import com.wile.main.base.DataBindingActivity
import com.wile.main.databinding.ActivityMainBinding
import com.wile.main.ui.adapter.TrainingAdapter
import com.wile.main.ui.add.AddActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity : DataBindingActivity() {

    val viewModel: MainViewModel by viewModels()
    private val binding: ActivityMainBinding by binding(R.layout.activity_main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.apply {
            lifecycleOwner = this@MainActivity
            adapter = TrainingAdapter()
            vm = viewModel
        }
        fab.setOnClickListener {
            val intent = Intent(this, AddActivity::class.java)
            startActivity(intent)
        }
    }
}