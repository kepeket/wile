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
import okhttp3.WebSocket
import timber.log.Timber

@AndroidEntryPoint
class JoinActivity : DataBindingActivity(), WileSocketListener {
    private val binding: ActivitySocialJoinBinding by binding(R.layout.activity_social_join)
    private val listenerImpl = WileSocketListenerImpl(
        onOpen = ::onOpen,
        onClosed = ::onClosed,
        onMessage = ::onMessage,
        onFailure = ::onFailure,
    )
    private val viewModel: JoinViewModel by viewModels()
    private lateinit var  bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.setWebSocketListener(listenerImpl)

        viewModel.connect()

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


    private fun onOpen(response: Response) {
        Timber.d(response.message())
    }

    private fun onMessage(type: EnvelopType, response: WileMessage) {
        var text = ""
        when(type){
            EnvelopType.Room -> {
                with(response as JoinRoomModels.JoinRoomMessage){
                    text = when(response.action){
                        RoomMessageAction.Joined -> {
                            getString(R.string.room_someone_has_joined, this.userId, this.name)
                        }
                        RoomMessageAction.Created -> {
                            getString(R.string.room_created, this.name)
                        }
                    }
                }
            }
            EnvelopType.Pong -> {
                with(response as PingModels.PongMessage){
                    text = getString(R.string.pong_received)
                }
            }
            else -> {
                text = getString(R.string.ws_unknown_message)
            }
        }
        binding.connectionLog.setText(
            String.format("%s\n%s", binding.connectionLog.text.toString(), text)
        )
    }

    private fun onClosed(code: Int, reason: String) {
        toggleBottomSheet(false)
        Toast.makeText(this, getString(R.string.ws_connection_list, reason), Toast.LENGTH_SHORT).show()
    }

    private fun onFailure(t: Throwable, response: Response?) {
        toggleBottomSheet(false)
        Toast.makeText(this, t.message, Toast.LENGTH_SHORT).show()
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
