package com.wile.app.ui.social

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.wile.app.R
import com.wile.app.base.DataBindingActivity
import com.wile.app.databinding.ActivitySocialJoinBinding
import com.wile.core.extensions.showToast
import com.wile.app.ui.adapter.RoomMemberAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class JoinActivity: DataBindingActivity() {
    private val binding: ActivitySocialJoinBinding by binding(R.layout.activity_social_join)

    var inputMethodManager: InputMethodManager? = null
        @Inject set
    private val viewModel: SocialWorkoutViewModel by viewModels()
    private var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>? = null
    private val adapter by lazy { RoomMemberAdapter() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                showToast(R.string.empty_userid)
                return@setOnClickListener
            }
            inputMethodManager?.hideSoftInputFromWindow(binding.root.windowToken, 0);
            viewModel.create(binding.username.text.toString())
        }

        binding.join.setOnClickListener {
            if (binding.username.text.isNullOrEmpty()) {
                showToast(R.string.empty_userid)
                return@setOnClickListener
            } else if (binding.room.text.isNullOrEmpty()) {
                showToast(R.string.empty_room_name)
                return@setOnClickListener
            }
            inputMethodManager?.hideSoftInputFromWindow(binding.root.windowToken, 0);
            viewModel.join(
                binding.username.text.toString(),
                binding.room.text.toString()
            )
        }

        binding.cancelSocialBtn.setOnClickListener {
            viewModel.leaveRoom()
        }

        binding.goSocialBtn.setOnClickListener {
            finish()
        }

        binding.roomShareBtn.setOnClickListener {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_room_text, viewModel.roomName.value))
                type = "text/plain"
            }
            startActivity(sendIntent)
        }

        viewModel.isInRoom.observe(this, {
            toggleBottomSheet(it)
        })
    }

    private fun toggleBottomSheet(toggle: Boolean){
        bottomSheetBehavior?.let {
            Handler(Looper.getMainLooper()).postDelayed({
                if (it.state == BottomSheetBehavior.STATE_EXPANDED && !toggle) {
                    it.state = BottomSheetBehavior.STATE_COLLAPSED
                } else if (it.state == BottomSheetBehavior.STATE_COLLAPSED && toggle) {
                    it.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }, 500)
        }
    }

    // UI based on connection status
    private fun onConnectionOpen() {
    }

    /*
    private fun onConnectionClosed(code: Int, reason: String) {
        runOnUiThread {
            showToast(getString(R.string.ws_connection_list, reason))
        }
    }

    private fun onConnectionFailure(t: Throwable, response: Response?) {
        t.message?.let {
            runOnUiThread {
                showToast(it)
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
            showToast(str)
            val textView = TextView(this)
            textView.text = str
            binding.connectionLog.addView(textView)
            textView.requestFocus()
        }
    }

    private fun cleanLogOutput(){
        binding.connectionLog.removeAllViews()
    }*/

    companion object {
        private const val WORKOUT_ID = "workout_id"

        fun newIntent(context: Context) = Intent(context, JoinActivity::class.java)
        fun startWorkout(context: Context, workoutId: Int) = newIntent(context).apply {
            putExtra(WORKOUT_ID, workoutId)
        }
    }
}
