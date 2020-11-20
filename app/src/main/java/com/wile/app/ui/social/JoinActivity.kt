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

    fun toggleBottomSheet(toggle: Boolean){
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED && !toggle){
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        } else if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED && toggle){
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }


    fun onOpen(response: Response) {
        Timber.d(response.message())
    }

    fun onMessage(text: String) {
        binding.connectionLog.setText(
            String.format("%s\n%s", binding.connectionLog.text.toString(), text)
        )
    }

    fun onClosed(code: Int, reason: String) {
        toggleBottomSheet(false)
        Toast.makeText(this, getString(R.string.ws_connection_list, reason), Toast.LENGTH_SHORT).show()
    }

    fun onFailure(t: Throwable, response: Response?) {
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
