package com.wile.app.ui.workout

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.widget.Chronometer
import androidx.activity.viewModels
import com.wile.app.R
import com.wile.app.base.DataBindingActivity
import com.wile.app.databinding.ActivityWorkoutBinding
import com.wile.app.sound.WorkoutSoundPlayer
import com.wile.app.ui.handler.WorkoutInterface
import com.wile.database.model.Training
import com.wile.database.model.TrainingTypes
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.bottom_sheet.view.*
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class WorkoutActivity : DataBindingActivity(), WorkoutInterface {

    private val viewModel: WorkoutViewModel by viewModels()
    private val binding: ActivityWorkoutBinding by binding(R.layout.activity_workout)

    private lateinit var chronoBind: Chronometer
    private lateinit var expendedTrainings: List<Training>
    private var currentTraining = -1
    private var trainingCountdown = 0
    private var lastTrainingChange = 0L
    private var timeWhenPaused = 0L
    override var chronometerIsRunning = false

    var vibrator: Vibrator? = null
        @Inject set
    @Inject
    lateinit var workoutSoundPlayer: WorkoutSoundPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding.apply {
            lifecycleOwner = this@WorkoutActivity
            viewModel = this@WorkoutActivity.viewModel
        }

        binding.workoutGo.apply {
            workoutController = this@WorkoutActivity
        }

        chronoBind = binding.workoutGo.trainingGoBottomSheet.chronometer
        chronoBind.format = getString(R.string.chronometer_format)


        viewModel.fetchTrainings(intent.getIntExtra(WORKOUT_ID, 0))
        viewModel.trainingListLiveData.observe(this, {
            expendedTrainings = viewModel.getExpendedTrainingList()
        })
    }
    
    /* Handle a workout
   Features:
    - 1 chronometer for timed training
    - 1 chronometer for estimated time until end of workout
    - when rep training, show a modal to finish the period
    - expend tabata training automatically
    - stop, pause, skip
*/
    override fun startWorkout(){
        expendedTrainings = viewModel.getExpendedTrainingList()

        binding.workoutGo.trainingGoBottomSheet.workout_progress.max = expendedTrainings.count()
        binding.workoutGo.trainingGoBottomSheet.workout_progress.progress = 0

        currentTraining = -1
        skipTraining()
        chronometerIsRunning = true
        chronoBind.base = SystemClock.elapsedRealtime()
        chronoBind.start()

    }

    override fun stopWorkout() {
        if (chronometerIsRunning) {
            chronoBind.stop()
            chronometerIsRunning = false
            currentTraining = -1
        }

        workoutSoundPlayer.playBell()

    }

    override fun pauseWorkout() {
        if (chronometerIsRunning) {
            timeWhenPaused = SystemClock.elapsedRealtime()
            binding.workoutGo.trainingGoBottomSheet.pause.setImageResource(R.drawable.ic_baseline_play_arrow_24)
        } else {
            lastTrainingChange = SystemClock.elapsedRealtime() - (timeWhenPaused - lastTrainingChange)
            binding.workoutGo.trainingGoBottomSheet.pause.setImageResource(R.drawable.ic_baseline_pause_24)
        }

        chronometerIsRunning = !chronometerIsRunning
    }

    override fun skipTraining() {
        currentTraining++
        if (currentTraining > 0) {
            notifyNewTraining()
        }
        if (currentTraining < expendedTrainings.count() -1) {
            displayTrainingInfo()
            binding.workoutGo.trainingGoBottomSheet.trainingCountdown.text = expendedTrainings[currentTraining].duration.toString()
            binding.workoutGo.trainingGoBottomSheet.workout_progress.progress = currentTraining
            binding.workoutGo.trainingGoBottomSheet.frame_cardview.visibility = View.GONE
            trainingCountdown = expendedTrainings[currentTraining].duration
            lastTrainingChange = SystemClock.elapsedRealtime()
        } else {
            stopWorkout()
        }
    }

    private fun displayTrainingInfo(){
        binding.workoutGo.trainingGoBottomSheet.currentWorkoutInfo.text =
            expendedTrainings[currentTraining].name
        if (currentTraining > 0){
            binding.workoutGo.trainingGoBottomSheet.currenTrainDescription.text =
                when (expendedTrainings[currentTraining].trainingType) {
                    TrainingTypes.Timed -> {
                        getString(R.string.training_timed_description)
                    }
                    TrainingTypes.Repeated -> {
                        getString(
                            R.string.training_repeated_description,
                            expendedTrainings[currentTraining].reps
                        )
                    }
                    TrainingTypes.Custom -> {
                        if (expendedTrainings[currentTraining].reps != 0) {
                            getString(
                                R.string.training_repeated_description,
                                expendedTrainings[currentTraining].reps
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

    override fun chronometerTicking(chronometer: Chronometer) {
        val elapsedTime = if (chronometerIsRunning) {
            ((SystemClock.elapsedRealtime() - lastTrainingChange) / 1000.0).roundToInt()
        } else {
            ((timeWhenPaused - lastTrainingChange) / 1000.0).roundToInt()
        }
        val endOfTraining = trainingCountdown - elapsedTime
        if (expendedTrainings[currentTraining].trainingType is TrainingTypes.Repeated) {
            if (endOfTraining < 5) {
                binding.workoutGo.trainingGoBottomSheet.frame_cardview.visibility = View.VISIBLE
            }
        }
        binding.workoutGo.trainingGoBottomSheet.trainingCountdown.text = endOfTraining.toString()
        if (endOfTraining in 1..4 && chronometerIsRunning) {
            workoutSoundPlayer.playBeep()
        }
        if (endOfTraining <= 0) {
            if (expendedTrainings[currentTraining].trainingType != TrainingTypes.Repeated) {
                skipTraining()
            } else {
                binding.workoutGo.trainingGoBottomSheet.trainingCountdown.text =
                    getString(R.string.workout_go_till_the_end)
            }
        }
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