package com.wile.app.ui.social

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.wile.app.R
import com.wile.app.base.DataBindingActivity
import com.wile.app.databinding.ActivitySocialJoinBinding
import com.wile.app.model.*
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.Response
import timber.log.Timber

@AndroidEntryPoint
class JoinActivity : DataBindingActivity() {
    private val binding: ActivitySocialJoinBinding by binding(R.layout.activity_social_join)

    private val viewModel: JoinViewModel by viewModels()
    private lateinit var  bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.setSocialWorkoutCallbackListener(
            WileSocketListenerCallback(
                onConnectionFailure = ::onConnectionFailure,
                onRoomCreated = ::onRoomCreated,
                onUserJoined = ::onUserJoined,
                onUserLeft = ::onUserLeft,
                connectionClosed = ::onConnectionClosed,
                connectionOpen = ::onConnectionOpen,
                onPong = ::onPongReceived
            )
        )

        binding.apply {
            lifecycleOwner = this@JoinActivity
            viewModel = this@JoinActivity.viewModel
        }

        bottomSheetBehavior = BottomSheetBehavior.from(binding.connectionBottomSheet)

        setSupportActionBar(binding.mainToolbar.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.create.setOnClickListener {
            toggleBottomSheet(true)
            if (!viewModel.create()) {
                Toast.makeText(this, getString(R.string.empty_userid), Toast.LENGTH_SHORT).show()
            }
        }

        binding.join.setOnClickListener {
            toggleBottomSheet(true)
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

        binding.cancelSocialBtn.setOnClickListener {
            toggleBottomSheet(false)
            viewModel.disconnect()
        }
    }

    private fun toggleBottomSheet(toggle: Boolean){
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED && !toggle){
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        } else if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED && toggle){
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    // UI based on connection status
    private fun onConnectionOpen() {
    }

    private fun onConnectionClosed(code: Int, reason: String) {
        toggleBottomSheet(false)
        Toast.makeText(this, getString(R.string.ws_connection_list, reason), Toast.LENGTH_SHORT).show()
    }

    private fun onConnectionFailure(t: Throwable, response: Response?) {
        toggleBottomSheet(false)
        t.message?.let {
            runOnUiThread {
                Toast.makeText(this, t.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // UI based on room activity
    private fun onUserJoined(userId: String, room: String){
        logOutput(getString(R.string.room_someone_has_joined, userId, room))
    }

    private fun onUserLeft(userId: String, room: String){
        logOutput(getString(R.string.room_someone_has_left, userId, room))
    }

    private fun onRoomCreated(room: String){
        logOutput(getString(R.string.room_created, room))
    }

    private fun onPongReceived(time: Long){

    }

    private fun logOutput(str: String){
        binding.connectionLog.setText(
            String.format("%s\n%s", binding.connectionLog.text.toString(), str)
        )
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
