package com.wile.app.ui.social

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
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
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val WORKOUT_ID = "workout_id"

        fun newIntent(context: Context) = Intent(context, JoinActivity::class.java)
        fun startWorkout(context: Context, workoutId: Int) = newIntent(context).apply {
            putExtra(WORKOUT_ID, workoutId)
        }
    }
}
