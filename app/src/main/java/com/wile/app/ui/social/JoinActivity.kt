package com.wile.app.ui.social

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.wile.app.R
import com.wile.app.base.DataBindingActivity
import com.wile.app.databinding.ActivitySocialJoinBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JoinActivity : DataBindingActivity() {
    private val viewModel: JoinViewModel by viewModels()
    private val binding: ActivitySocialJoinBinding by binding(R.layout.activity_social_join)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.apply {
            lifecycleOwner = this@JoinActivity
            viewModel = this@JoinActivity.viewModel
        }

        setSupportActionBar(binding.mainToolbar.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.create.setOnClickListener {
            if (!viewModel.create()) {
                Toast.makeText(this, getString(R.string.empty_userid), Toast.LENGTH_SHORT).show()
            }
        }

        binding.join.setOnClickListener {
            if (!viewModel.join()) {
                if (viewModel.userName.value.isNullOrEmpty()) {
                    Toast.makeText(this, getString(R.string.empty_userid), Toast.LENGTH_SHORT)
                        .show()
                } else if (viewModel.roomNameInput.value.isNullOrEmpty()) {
                    Toast.makeText(this, getString(R.string.empty_room_name), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.connect()
    }

    override fun onPause() {
        super.onPause()
        viewModel.disconnect()
    }

    companion object {
        const val WORKOUT_ID = "workout_id"

        fun newIntent(context: Context) = Intent(context, JoinActivity::class.java)
        fun startWorkout(context: Context, workoutId: Int) = newIntent(context).apply {
            putExtra(WORKOUT_ID, workoutId)
        }
    }
}
