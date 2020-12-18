package com.wile.app.ui.workout

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.ServiceConnection
import android.os.*
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import com.wile.app.R
import com.wile.core.databinding.DataBindingActivity
import com.wile.app.databinding.ActivityWorkoutBinding
import com.wile.app.services.WorkoutService
import com.wile.app.sound.WorkoutSoundPlayer
import com.wile.app.ui.handler.WorkoutInterface
import com.wile.core.extensions.showToast
import com.wile.database.model.Training
import com.wile.database.model.TrainingTypes
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.workout_controller.view.*
import javax.inject.Inject

@AndroidEntryPoint
class WorkoutActivity : DataBindingActivity(), WorkoutInterface {

    private val viewModel: WorkoutViewModel by viewModels()
    private val binding: ActivityWorkoutBinding by binding(R.layout.activity_workout)
    private var currentTrainingCache = -1
    private var socialMode = false
    private var isHost = false

    // Service binding
    private var workoutService: WorkoutService? = null

    // Social
    private var askLobby = false

    private var cachedTrainings: List<Training> = listOf()

    var vibrator: Vibrator? = null
        @Inject set

    @Inject
    lateinit var workoutSoundPlayer: WorkoutSoundPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.apply {
            viewModel = this@WorkoutActivity.viewModel
        }

        binding.workoutGo.apply {
            workoutController = this@WorkoutActivity
        }

        socialMode = intent.getBooleanExtra(SOCIAL_MODE, false)
        isHost = intent.getBooleanExtra(SOCIAL_HOST, false)
        if ((isHost && socialMode) || !socialMode) {
            viewModel.fetchTrainings(intent.getIntExtra(WORKOUT_ID, 0))
            viewModel.trainingListLiveData.observe(this, {
                cachedTrainings = viewModel.getExpendedTrainingList()
                workoutService?.setTrainingList(cachedTrainings)
                binding.workoutGo.trainingGoBottomSheet.workout_progress.max =
                    cachedTrainings.count()
            })
            askLobby = socialMode
        } else {
            if (socialMode && !isHost) {
                binding.workoutGo.trainingGoBottomSheet.next.visibility = View.GONE
                binding.workoutGo.trainingGoBottomSheet.prepareText.text =
                    getString(R.string.social_ready_to_begin)
            }
        }

        // bind the workout service
        Intent(this, WorkoutService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    // Service binding callback
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as WorkoutService.WorkoutBinder
            workoutService = binder.getService()
            workoutService?.let { svc ->
                if (cachedTrainings.count() > 1) {
                    svc.setTrainingList(cachedTrainings)
                }
                if (askLobby){
                    svc.askLobby()
                } else {
                    svc.tellReady()
                }
                // The main ticking event
                svc.countdownLiveData.observe(this@WorkoutActivity, { countdown ->
                    if (svc.currentTrainingLiveData.value?.trainingType == TrainingTypes.Repeated) {
                        if (countdown < 5 && svc.chronometerIsRunningLiveData.value == true) {
                            binding.workoutGo.trainingGoBottomSheet.frame_cardview.visibility = View.VISIBLE
                        }
                    }
                    binding.workoutGo.trainingGoBottomSheet.trainingCountdown.text =
                        if (countdown < 0) {
                            if (svc.currentTrainingLiveData.value?.trainingType == TrainingTypes.Repeated) {
                                    getString(R.string.workout_go_till_the_end)
                            } else {
                                ""
                            }
                        } else {
                            countdown.toString()
                        }
                    if (countdown in 1..4) {
                        workoutSoundPlayer.playBeep()
                    }
                })
                svc.elapsedTimeLiveData.observe(this@WorkoutActivity, { time ->
                    binding.workoutGo.trainingGoBottomSheet.chronometer.text = getString(R.string.chronometer_format,
                            durationToStr(time)
                    )
                })
                svc.workoutProgressMaxLiveData.observe(this@WorkoutActivity, { max ->
                    binding.workoutGo.trainingGoBottomSheet.workout_progress.max = max
                })
                svc.workoutProgressLiveData.observe(this@WorkoutActivity, { position ->
                    currentTrainingCache = position
                    if (position >= 0) {
                        binding.workoutGo.trainingGoBottomSheet.prepareText.visibility = View.GONE
                        binding.workoutGo.trainingGoBottomSheet.pause.setImageResource(R.drawable.ic_baseline_pause_24)
                        binding.workoutGo.trainingGoBottomSheet.workout_progress.progress = position
                        binding.workoutGo.trainingGoBottomSheet.frame_cardview.visibility = View.GONE
                        if (position > 0) {
                            notifyNewTraining()
                        }
                    }
                })
                svc.workoutIsDoneLiveData.observe(this@WorkoutActivity, { done ->
                    if (done && currentTrainingCache >= 0) {
                        workoutStopped()
                    }
                })
                svc.currentTrainingLiveData.observe(this@WorkoutActivity, { training ->
                    training?.let {
                        val bgColor = when (training.trainingType) {
                            TrainingTypes.Timed -> getColor(R.color.training_go_blue)
                            TrainingTypes.Repeated -> getColor(R.color.repeated_preset)
                            TrainingTypes.Tabata -> getColor(R.color.tabata_preset)
                            TrainingTypes.Custom -> getColor(R.color.dark_grey_variant)
                        }
                        binding.workoutGo.trainingGoBottomSheet.setBackgroundColor(bgColor)
                        binding.workoutGo.trainingGoBottomSheet.currentWorkoutInfo.text =
                            training.name
                        if (svc.workoutProgressLiveData.value!! > 0) {
                            binding.workoutGo.trainingGoBottomSheet.currenTrainDescription.text =
                                when (training.trainingType) {
                                    TrainingTypes.Timed -> {
                                        getString(R.string.training_timed_description)
                                    }
                                    TrainingTypes.Repeated -> {
                                        getString(
                                            R.string.training_repeated_description,
                                            training.reps
                                        )
                                    }
                                    TrainingTypes.Custom -> {
                                        if (training.reps != 0) {
                                            getString(
                                                R.string.training_repeated_description,
                                                training.reps
                                            )
                                        } else {
                                            getString(R.string.training_timed_description)
                                        }
                                    }
                                    else -> {
                                        ""
                                    }
                                }
                        }
                    }
                })
                svc.chronometerIsRunningLiveData.observe(this@WorkoutActivity, { running ->
                    if (!running) {
                        binding.workoutGo.trainingGoBottomSheet.pause.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                    } else {
                        if (binding.workoutGo.prepareText.visibility != View.GONE){
                            binding.workoutGo.prepareText.visibility = View.GONE
                        }
                        binding.workoutGo.trainingGoBottomSheet.pause.setImageResource(R.drawable.ic_baseline_pause_24)
                    }
                })
            }

        }

        override fun onServiceDisconnected(arg0: ComponentName) {
        }
    }

    override fun askStartPauseWorkout() {
        workoutService?.let {
            when(it.startStopWorkout()) {
                WorkoutService.WorkoutError.NotHost -> {
                    showToast(R.string.social_start_pause_denied_not_host)
                }
                WorkoutService.WorkoutError.NoopError -> {}
                WorkoutService.WorkoutError.NoTraining -> {
                    showToast(R.string.no_excercice)
                }
            }
        } ?: run {
            showToast(R.string.no_workout_service)
        }
    }

    override fun askStopWorkout() {
        workoutStopped()
        workoutService?.stopWorkout()
    }

    override fun askSkipTraining() {
        // catch return to toast
        when (workoutService?.skipTraining()){
            WorkoutService.WorkoutError.NotHost -> {
                showToast(R.string.social_skip_denied_not_host)
            }
            else -> {
                binding.workoutGo.trainingGoBottomSheet.frame_cardview.visibility = View.GONE
            }
        }

    }

    fun workoutStopped() {
        workoutSoundPlayer.playBell()
        finish()
    }

    private fun durationToStr(duration: Int): String {
        val min = duration / 60
        val sec = duration % 60
        return String.format("%d:%02d", min, sec)
    }

    private fun notifyNewTraining() {
        workoutSoundPlayer.playWhistle()

        vibrator?.vibrate(VibrationEffect.createOneShot(VIBRATION_TIME, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    companion object {
        const val VIBRATION_TIME = 500L
        const val WORKOUT_ID = "workout_id"
        const val SOCIAL_MODE = "social_mode"
        const val SOCIAL_HOST = "social_host"

        fun newIntent(context: Context) = Intent(context, WorkoutActivity::class.java)
        fun startWorkout(context: Context, workoutId: Int) = newIntent(context).apply {
            putExtra(WORKOUT_ID, workoutId)
        }
        fun startSocialWorkout(context: Context, workoutId: Int, host: Boolean) = newIntent(context).apply {
            putExtra(SOCIAL_MODE, true)
            putExtra(WORKOUT_ID, workoutId)
            putExtra(SOCIAL_HOST, host)
            addFlags(FLAG_ACTIVITY_NEW_TASK)
        }
    }
}