package com.wile.app.ui.social

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.wile.app.R
import com.wile.app.base.DataBindingActivity
import com.wile.app.databinding.ActivitySocialJoinBinding
import com.wile.app.ui.adapter.RoomMemberAdapter
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.Response

@AndroidEntryPoint
class JoinActivity : DataBindingActivity() {
    private val binding: ActivitySocialJoinBinding by binding(R.layout.activity_social_join)

    private val viewModel: SocialWorkoutViewModel by viewModels()
    private var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>? = null
    private val adapter by lazy { RoomMemberAdapter() }

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
                onPong = ::onPongReceived,
                onError = ::onError
            )
        )

        binding.apply {
            lifecycleOwner = this@JoinActivity
            viewModel = this@JoinActivity.viewModel
            memberAdapter = adapter
        }

        bottomSheetBehavior = BottomSheetBehavior.from(binding.connectionBottomSheet)

        setSupportActionBar(binding.mainToolbar.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.create.setOnClickListener {
            if (binding.username.text.isNullOrEmpty()) {
                Toast.makeText(this, getString(R.string.empty_userid), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.create(binding.username.text.toString())
        }

        binding.join.setOnClickListener {
            if (binding.username.text.isNullOrEmpty()) {
                Toast.makeText(this, getString(R.string.empty_userid), Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            } else if (binding.room.text.isNullOrEmpty()) {
                Toast.makeText(this, getString(R.string.empty_room_name), Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            viewModel.join(binding.username.text.toString(),
                binding.room.text.toString())
        }

        binding.cancelSocialBtn.setOnClickListener {
            viewModel.disconnect()
        }

        binding.goSocialBtn.setOnClickListener {
            finish()
        }

        viewModel.refreshConnectionStatus()

        viewModel.isInRoom.observe(this, {
            toggleBottomSheet(it)
        })
    }

    override fun onPause() {
        super.onPause()
        viewModel.saveState()
    }

    private fun toggleBottomSheet(toggle: Boolean){
        bottomSheetBehavior?.let {
            Handler(Looper.getMainLooper()).postDelayed({
                if (it.state == BottomSheetBehavior.STATE_EXPANDED && !toggle){
                    it.state = BottomSheetBehavior.STATE_COLLAPSED
                } else if (it.state == BottomSheetBehavior.STATE_COLLAPSED && toggle){
                    it.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }, 500)
        }
    }

    // UI based on connection status
    private fun onConnectionOpen() {
    }

    private fun onConnectionClosed(code: Int, reason: String) {
        runOnUiThread {
            Toast.makeText(this, getString(R.string.ws_connection_list, reason), Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun onConnectionFailure(t: Throwable, response: Response?) {
        t.message?.let {
            runOnUiThread {
                Toast.makeText(this, t.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // UI based on room activity
    private fun onUserJoined(userId: String, room: String){
        logOutput(getString(R.string.room_someone_has_joined, userId))
    }

    private fun onUserLeft(userId: String, room: String){
        logOutput(getString(R.string.room_someone_has_left, userId))
    }

    private fun onRoomCreated(room: String){
        logOutput(getString(R.string.room_created, room))
    }

    private fun onPongReceived(time: Long){

    }

    private fun onError(message: String){
        val ald = AlertDialog.Builder(this).create()
        ald.setMessage(message)
        ald.show()
    }

    private fun logOutput(str: String){
        runOnUiThread {
            Toast.makeText(this, str, Toast.LENGTH_SHORT).show()
            val textView = TextView(this)
            textView.text = str
            binding.connectionLog.addView(textView)
            textView.requestFocus()
        }
    }

    private fun cleanLogOutput(){
        binding.connectionLog.removeAllViews()
    }

    companion object {
        const val WORKOUT_ID = "workout_id"

        fun newIntent(context: Context) = Intent(context, JoinActivity::class.java)
        fun startWorkout(context: Context, workoutId: Int) = newIntent(context).apply {
            putExtra(WORKOUT_ID, workoutId)
        }
    }
}
