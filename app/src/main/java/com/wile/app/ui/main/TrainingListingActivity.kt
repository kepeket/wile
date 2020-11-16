package com.wile.app.ui.main

import android.os.Bundle
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Chronometer
import androidx.activity.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.wile.app.R
import com.wile.app.base.DataBindingActivity
import com.wile.app.databinding.ActivityTrainingListingBinding
import com.wile.database.model.Training
import com.wile.database.model.TrainingTypes
import com.wile.app.sound.WorkoutSoundPlayer
import com.wile.app.ui.adapter.TrainingAdapter
import com.wile.app.ui.add.AddActivity
import com.wile.app.ui.add.QuickAddActivity
import com.wile.app.ui.add.TabataAddActivity
import com.wile.app.ui.handler.WorkoutInterface
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_training_listing.*
import kotlinx.android.synthetic.main.bottom_sheet.view.*
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class TrainingListingActivity : DataBindingActivity(), WorkoutInterface {

    private val viewModel: TrainingListingViewModel by viewModels()
    private val binding: ActivityTrainingListingBinding by binding(R.layout.activity_training_listing)
    private val adapter by lazy { TrainingAdapter(
        onDeleteTraining = ::onDeleteTraining,
        onMoveTraining =  ::onMoveTraining,
        onTouchTraining = ::onTouchTraining
    )}
    private lateinit var chronoBind: Chronometer
    private lateinit var expendedTrainings: List<Training>
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private var currentWorkout = -1
    private var trainingCountdown = 0
    private var lastTrainingChange = 0L
    private var timeWhenPaused = 0L
    override var chronometerIsRunning = false

    var vibrator: Vibrator? = null
        @Inject set
    @Inject lateinit var workoutSoundPlayer: WorkoutSoundPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding.apply {
            lifecycleOwner = this@TrainingListingActivity
            adapter = this@TrainingListingActivity.adapter
            viewModel = this@TrainingListingActivity.viewModel
        }

        binding.workoutGo.apply {
            workoutController = this@TrainingListingActivity
        }

        chronoBind = binding.workoutGo.trainingGoBottomSheet.chronometer
        chronoBind.format = getString(R.string.chronometer_format)
        bottomSheetBehavior = BottomSheetBehavior.from(binding.workoutGo.trainingGoBottomSheet)

        bottomSheetBehavior.isHideable = false
        bottomSheetBehavior.isDraggable = false

        setSupportActionBar(binding.mainToolbar.toolbar)
        fab.setOnClickListener {
            startActivity(QuickAddActivity.newIntent(this))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.training_main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.training_main_go -> {
            startWorkout()
            true
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    private fun onDeleteTraining(training: Training) {
        viewModel.deleteTraining(training.id)
    }

    private fun onMoveTraining(trainings: List<Training>) {
        viewModel.saveTrainings(trainings)
    }

    private fun onTouchTraining(training: Training) {
        when(training.trainingType){
            TrainingTypes.Tabata -> {
                startActivity(TabataAddActivity.editTabata(this, training.id))
            }
            else -> {
                startActivity(AddActivity.editTraining(this, training.id))
            }
        }
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

        val sheetBehavior = BottomSheetBehavior.from(binding.workoutGo.trainingGoBottomSheet)

        if (sheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        binding.workoutGo.trainingGoBottomSheet.workout_progress.max = expendedTrainings.count()
        binding.workoutGo.trainingGoBottomSheet.workout_progress.progress = 0

        currentWorkout = -1
        skipTraining()
        chronometerIsRunning = true
        chronoBind.base = SystemClock.elapsedRealtime()
        chronoBind.start()

    }

    override fun stopWorkout() {
        if (chronometerIsRunning) {
            chronoBind.stop()
            chronometerIsRunning = false
            currentWorkout = -1
        }

        workoutSoundPlayer.playBell()

        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
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
        currentWorkout++
        if (currentWorkout > 0) {
            notifyNewTraining()
        }
        if (currentWorkout < expendedTrainings.count() -1) {
            displayTrainingInfo()
            binding.workoutGo.trainingGoBottomSheet.trainingCountdown.text = expendedTrainings[currentWorkout].duration.toString()
            binding.workoutGo.trainingGoBottomSheet.workout_progress.progress = currentWorkout
            binding.workoutGo.trainingGoBottomSheet.frame_cardview.visibility = View.GONE
            trainingCountdown = expendedTrainings[currentWorkout].duration
            lastTrainingChange = SystemClock.elapsedRealtime()
        } else {
            stopWorkout()
        }
    }

    private fun displayTrainingInfo(){
        binding.workoutGo.trainingGoBottomSheet.currentWorkoutInfo.text =
            expendedTrainings[currentWorkout].name
        if (currentWorkout > 0){
            binding.workoutGo.trainingGoBottomSheet.currenTrainDescription.text =
                when (expendedTrainings[currentWorkout].trainingType) {
                    TrainingTypes.Timed -> {
                        getString(R.string.training_timed_description)
                    }
                    TrainingTypes.Repeated -> {
                        getString(
                            R.string.training_repeated_description,
                            expendedTrainings[currentWorkout].reps
                        )
                    }
                    TrainingTypes.Custom -> {
                        if (expendedTrainings[currentWorkout].reps != 0) {
                            getString(
                                R.string.training_repeated_description,
                                expendedTrainings[currentWorkout].reps
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
        if (expendedTrainings[currentWorkout].trainingType is TrainingTypes.Repeated) {
            if (endOfTraining < 5) {
                binding.workoutGo.trainingGoBottomSheet.frame_cardview.visibility = View.VISIBLE
            }
        }
        binding.workoutGo.trainingGoBottomSheet.trainingCountdown.text = endOfTraining.toString()
        if (endOfTraining in 1..4 && chronometerIsRunning) {
            workoutSoundPlayer.playBeep()
        }
        if (endOfTraining <= 0) {
            if (expendedTrainings[currentWorkout].trainingType != TrainingTypes.Repeated) {
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
    }
}
