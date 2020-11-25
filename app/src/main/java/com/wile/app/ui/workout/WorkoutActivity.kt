package com.wile.app.ui.workout

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.view.View
import android.widget.Chronometer
import android.widget.Toast
import androidx.activity.viewModels
import com.google.android.gms.common.util.Strings
import com.wile.app.R
import com.wile.app.base.DataBindingActivity
import com.wile.app.databinding.ActivityWorkoutBinding
import com.wile.app.extensions.showToast
import com.wile.app.sound.WorkoutSoundPlayer
import com.wile.app.ui.handler.WorkoutInterface
import com.wile.database.model.Training
import com.wile.database.model.TrainingTypes
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.workout_controller.view.*
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class WorkoutActivity : DataBindingActivity(), WorkoutInterface {

    private val viewModel: WorkoutViewModel by viewModels()
    private val binding: ActivityWorkoutBinding by binding(R.layout.activity_workout)

    // Service binding
    private var workoutService: WorkoutService? = null
    private var serviceBound: Boolean = false

    private var cachedTrainings: List<Training> = listOf()

    var vibrator: Vibrator? = null
        @Inject set
    @Inject
    lateinit var workoutSoundPlayer: WorkoutSoundPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // bind the workout service
        Intent(this, WorkoutService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }

        binding.apply {
            lifecycleOwner = this@WorkoutActivity
            viewModel = this@WorkoutActivity.viewModel
        }

        binding.workoutGo.apply {
            workoutController = this@WorkoutActivity
        }

        viewModel.fetchTrainings(intent.getIntExtra(WORKOUT_ID, 0))
        viewModel.trainingListLiveData.observe(this, {
            cachedTrainings = viewModel.getExpendedTrainingList()
            workoutService?.setTrainingList(cachedTrainings)
        })
    }

    // Service binding callback
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as WorkoutService.WorkoutBinder
            workoutService = binder.getService()
            serviceBound = true
            workoutService?.setListener(this@WorkoutActivity)
            if (cachedTrainings.count() > 1){
                workoutService?.setTrainingList(cachedTrainings)
            }

        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            serviceBound = false
        }
    }

    override fun askStartPauseWorkout(){
         workoutService?.let {
             if (!it.startStopWorkout()) {
                 showToast(R.string.no_excercice)
             }
         } ?: run {
             showToast(R.string.no_workout_service)
         }
    }

    override fun askStopWorkout(){
        workoutService?.let {
            it.stopWorkout()
        } ?: run {
            workoutStopped()
        }
    }

    override fun askSkipTraining(){
        workoutService?.let {
            it.skipTraining()
        }
    }

    override fun workoutStarted(){
        binding.workoutGo.trainingGoBottomSheet.prepareText.visibility = View.GONE
        binding.workoutGo.trainingGoBottomSheet.pause.setImageResource(R.drawable.ic_baseline_pause_24)
        binding.workoutGo.trainingGoBottomSheet.workout_progress.max = cachedTrainings.count()
        binding.workoutGo.trainingGoBottomSheet.workout_progress.progress = 0
    }

    override fun workoutStopped() {
        workoutSoundPlayer.playBell()
        finish()
    }

    override fun workoutPaused(isPaused: Boolean) {
        if (!isPaused) {
            binding.workoutGo.trainingGoBottomSheet.pause.setImageResource(R.drawable.ic_baseline_play_arrow_24)
        } else {
            binding.workoutGo.trainingGoBottomSheet.pause.setImageResource(R.drawable.ic_baseline_pause_24)
        }
    }

    override fun trainingSkipped(needNotify: Boolean,
                                 trainingDuration: Int,
                                 progress: Int,
                                 newTraining: Training) {
        if (needNotify) {
            notifyNewTraining()
        }
        displayTrainingInfo(newTraining, progress==0)
        binding.workoutGo.trainingGoBottomSheet.trainingCountdown.text = trainingDuration.toString()
        binding.workoutGo.trainingGoBottomSheet.workout_progress.progress = progress
        binding.workoutGo.trainingGoBottomSheet.frame_cardview.visibility = View.GONE
    }

    private fun displayTrainingInfo(training: Training, warmup: Boolean){
        val bgColor = when(training.trainingType){
            TrainingTypes.Timed -> getColor(R.color.training_go_blue)
            TrainingTypes.Repeated -> getColor(R.color.repeated_preset)
            TrainingTypes.Tabata -> getColor(R.color.tabata_preset)
            TrainingTypes.Custom -> getColor(R.color.dark_grey_variant)
        }
        binding.workoutGo.trainingGoBottomSheet.setBackgroundColor(bgColor)

        binding.workoutGo.trainingGoBottomSheet.currentWorkoutInfo.text =
                training.name
        if (!warmup){
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

    override fun chronometerTicked(elapsedTime: Int, countdown: Int, trainingDuration: Int, trainingType: TrainingTypes) {
        if (trainingType is TrainingTypes.Repeated) {
            if (countdown < 5) {
                binding.workoutGo.trainingGoBottomSheet.frame_cardview.visibility = View.VISIBLE
            }
        }
        binding.workoutGo.trainingGoBottomSheet.chronometer.text = getString(R.string.chronometer_format,
                durationToStr(elapsedTime)
        )
        binding.workoutGo.trainingGoBottomSheet.trainingCountdown.text = countdown.toString()
        if (countdown in 1..4) {
            workoutSoundPlayer.playBeep()
        }
        if (countdown <= 0 && trainingType == TrainingTypes.Repeated) {
            binding.workoutGo.trainingGoBottomSheet.trainingCountdown.text =
                getString(R.string.workout_go_till_the_end)
        }
    }

    private fun durationToStr(duration: Int): String {
        val min = duration / 60
        val sec = duration % 60
        return String.format("%d:%d", min, sec)
    }

    private fun notifyNewTraining() {
        workoutSoundPlayer.playWhistle()

        vibrator?.vibrate(VibrationEffect.createOneShot(VIBRATION_TIME, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    companion object {
        const val VIBRATION_TIME = 500L
        const val WORKOUT_ID = "workout_id"

        fun newIntent(context: Context) = Intent(context, WorkoutActivity::class.java)
        fun startWorkout(context: Context, workoutId: Int) = newIntent(context).apply {
            putExtra(WORKOUT_ID, workoutId)
        }
    }
}