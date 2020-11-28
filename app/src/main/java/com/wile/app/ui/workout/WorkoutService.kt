package com.wile.app.ui.workout

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.*
import androidx.lifecycle.MutableLiveData
import com.tinder.scarlet.Lifecycle
import com.tinder.scarlet.ShutdownReason
import com.tinder.scarlet.lifecycle.LifecycleRegistry
import com.wile.app.model.*
import com.wile.app.ui.social.WileServer
import com.wile.database.model.Training
import com.wile.database.model.TrainingTypes
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.workout_controller.view.*
import org.hashids.Hashids
import timber.log.Timber
import java.time.Instant
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap
import kotlin.concurrent.fixedRateTimer
import kotlin.math.roundToInt
import kotlin.reflect.KParameter

@AndroidEntryPoint
class WorkoutService : Service() {

    @Inject
    lateinit var server: WileServer

    @Inject
    lateinit var scarletLifecycleRegistry: LifecycleRegistry

    private val binder = WorkoutBinder()

    private var expendedTrainings: List<Training> = listOf()
    private var currentTraining = -1
    private var trainingCountdown = 0
    private var lastTrainingChange = 0L
    private var timeWhenPaused = 0L
    private var timeWhenStarted = 0L
    private var chronometerIsRunning = false
    private var timer: Timer? = null
    private var timerClient: Timer? = null
    private val roomNameCreation = Hashids().encode(Instant.now().toEpochMilli() / 1000)
        .toUpperCase(Locale.ROOT).take(6)

    val countdownLiveData: MutableLiveData<Int> = MutableLiveData(-1)
    val elapsedTimeLiveData: MutableLiveData<Int> = MutableLiveData(0)
    val workoutProgressLiveData: MutableLiveData<Int> = MutableLiveData(currentTraining)
    val currentTrainingLiveData: MutableLiveData<Training> = MutableLiveData()
    val chronometerIsRunningLiveData: MutableLiveData<Boolean> =
        MutableLiveData(chronometerIsRunning)
    val workoutIsDoneLiveData: MutableLiveData<Boolean> = MutableLiveData(false)
    val workoutProgressMaxLiveData: MutableLiveData<Int> = MutableLiveData(0)

    // social LiveData
    val roomName: MutableLiveData<String> = MutableLiveData("")
    val userName: MutableLiveData<String> = MutableLiveData("")
    val roomMembers: MutableLiveData<HashMap<String, Boolean>> = MutableLiveData(hashMapOf())
    val readyMembers: MutableLiveData<MutableList<String>> = MutableLiveData(mutableListOf())
    val canStart: MutableLiveData<Boolean> = MutableLiveData(false)
    val isHost: MutableLiveData<Boolean> = MutableLiveData(false)
    val isInRoom: MutableLiveData<Boolean> = MutableLiveData(false)


    /**
     * @TODO
     *
     * When you join you should receive the member list
     */

    @SuppressLint("CheckResult")
    override fun onBind(p0: Intent?): IBinder? {
        scarletLifecycleRegistry.onNext(Lifecycle.State.Started)
        server.roomMessage().subscribe {
            roomMessageReceived(it.message)
        }
        server.workoutMessage().subscribe {
            workoutMessageReceived(it.message)
        }

        // subscribe to ping
        return binder
    }

    inner class WorkoutBinder : Binder() {
        fun getService(): WorkoutService = this@WorkoutService
    }

    // Social region

    fun createRoom(userId: String) {
        isHost.postValue(true)
        roomSubscription(userId, roomNameCreation, RoomMessageAction.Create)
    }


    fun joinRoom(userId: String, room: String) {
        isHost.postValue(false)
        roomSubscription(userId, room, RoomMessageAction.Join)
    }

    fun leaveRoom() {
        isInRoom.value?.let { inRoom ->
            if (inRoom) {
                isInRoom.postValue(false)
                roomSubscription(userName.value!!, roomName.value!!, RoomMessageAction.Leave)
                isHost.postValue(false)
                //scarletLifecycleRegistry.onNext(Lifecycle.State.Stopped.WithReason(ShutdownReason.GRACEFUL))
            }
        }
    }

    private fun roomSubscription(user: String, room: String, action: RoomMessageAction) {
        userName.postValue(user)
        roomName.postValue(room)
        val env = EnvelopRoom(
            message = RoomModels.RoomMessage(
                userId = user,
                name = room,
                action = action
            )
        )
        server.messageRoom(env)
    }

    private fun workoutMessageReceived(response: WorkoutModels.WorkoutMessage) {
        when (response.action) {
            WorkoutMessageAction.Paused -> {
                if (isHost.value == false) {
                    chronometerIsRunningLiveData.postValue(false)
                }
            }
            WorkoutMessageAction.Stopped -> {
                if (isHost.value == false) {
                    timerClient?.apply {
                        cancel()
                        purge()
                    }
                    timerClient = null
                    elapsedTimeLiveData.postValue(0)
                    stopWorkout()
                }
            }
            WorkoutMessageAction.Lobby -> {
                workoutIsDoneLiveData.postValue(false)
                if (isHost.value == false) {
                    // Asking to put yourself in lobby, so we'll launch the workout activity in social mode
                    startActivity(
                        WorkoutActivity.startSocialWorkout(
                            applicationContext,
                            0,
                            false
                        )
                    )
                }
            }
            WorkoutMessageAction.Ready -> {
                // People sending you ready only matters if you are a host
                if (isHost.value == true) {
                    readyMembers.value?.let { members ->
                        if (!members.contains(response.userId)) {
                            members.add(response.userId)
                        }
                        if (members.count() == roomMembers.value?.count()) {
                            canStart.postValue(true)
                        }
                    }
                }
            }
            WorkoutMessageAction.Started -> {
                if (isHost.value == false) {
                    timeWhenStarted = SystemClock.elapsedRealtime()
                    if (timerClient == null) {
                        timerClient = fixedRateTimer("timerClient", false, 0L, 1000) {
                            Timber.d("Chrono client")
                            elapsedTimeLiveData.postValue(((SystemClock.elapsedRealtime() - timeWhenStarted) / 1000.0).roundToInt())
                        }
                    }
                }
            }
            WorkoutMessageAction.Started,
            WorkoutMessageAction.TrainingStart -> {
                if (isHost.value == false) {
                    workoutIsDoneLiveData.postValue(false)
                    chronometerIsRunningLiveData.postValue(true)
                    // Reset all livedata on the new joined training
                    val training = Training(
                        name = response.training.name,
                        reps = response.training.reps,
                        repRate = response.training.repRate,
                        trainingType = response.training.trainingType
                    )
                    currentTrainingLiveData.postValue(training)
                    if (response.countdown > 0) {
                        countdownLiveData.postValue(response.countdown)
                    }
                    workoutProgressLiveData.postValue(response.trainingPos)
                    workoutProgressMaxLiveData.postValue(response.trainingCount)
                }
            }
            WorkoutMessageAction.Tick -> {
                if (isHost.value == false) {
                    countdownLiveData.postValue(response.countdown)
                }
            }
            else -> {
                Timber.i("Should not received this message %v", response)
            }
        }
    }

    private fun roomMessageReceived(response: RoomModels.RoomMessage) {
        when (response.action) {
            RoomMessageAction.Joined -> {
                if (response.userId == userName.value) {
                    isInRoom.postValue(true)
                    isHost.postValue(false)
                } else {
                    roomMembers.value?.let { orig ->
                        roomMembers.postValue(orig.also { it[response.userId] = true })
                    }
                }
                //callbackListener.onUserJoined(response.userId, response.name)
            }
            RoomMessageAction.Created -> {
                isInRoom.postValue(true)
                isHost.postValue(true)
            }
            RoomMessageAction.Left -> {
                roomMembers.value?.let { orig ->
                    if (response.userId == userName.value) {
                        isInRoom.postValue(false)
                        isHost.postValue(false)
                        roomMembers.postValue(orig.also { it.clear() })
                    } else {
                        roomMembers.postValue(orig.also { it.remove(response.userId) })
                    }
                }
            }
            else -> {
            }
        }
    }

    private fun sendWorkoutMessage(countdown: Int, action: WorkoutMessageAction) {
        if ((isHost.value == false && action != WorkoutMessageAction.Ready) || isInRoom.value == false) {
            return
        }
        var trainingLight: TrainingLight = TrainingLight()
        if (isHost.value == true &&
            action !in listOf(WorkoutMessageAction.Lobby)
            && currentTraining in 0 until expendedTrainings.count()
        ) {
            expendedTrainings[currentTraining].let {
                trainingLight = TrainingLight(
                    name = it.name,
                    reps = it.reps,
                    repRate = it.repRate,
                    trainingType = it.trainingType
                )
            }
        }
        val message = WorkoutModels.WorkoutMessage(
            userId = userName.value.orEmpty(),
            name = roomName.value.orEmpty(),
            countdown = countdown,
            trainingPos = currentTraining,
            trainingCount = expendedTrainings.count(),
            action = action,
            timestamp = Instant.now().toEpochMilli(),
            training = trainingLight
        )

        val env = EnvelopWorkout(
            message = message
        )
        server.messageWorkout(env)
    }

    fun askLobby() {
        sendWorkoutMessage(0, WorkoutMessageAction.Lobby)
    }

    fun tellReady() {
        sendWorkoutMessage(0, WorkoutMessageAction.Ready)
    }

    // Private region
    fun startStopWorkout(): WorkoutError {
        if (expendedTrainings.count() > 1) {
            if (isInRoom.value == true && isHost.value == false){
                return WorkoutError.NotHost
            }
            if (currentTraining < 0) {
                startWorkout()
            } else {
                pauseWorkout()
            }
            return WorkoutError.NoopError
        }
        return WorkoutError.NoTraining
    }

    private fun startWorkout() {
        currentTraining = -1
        workoutProgressLiveData.postValue(currentTraining)
        workoutIsDoneLiveData.postValue(false)
        elapsedTimeLiveData.postValue(0)
        chronometerIsRunning = true
        skipTraining()
        chronometerIsRunningLiveData.postValue(chronometerIsRunning)
        timeWhenStarted = SystemClock.elapsedRealtime()
        sendWorkoutMessage(expendedTrainings[currentTraining].duration, WorkoutMessageAction.Start)
        timer = fixedRateTimer("timer", false, 0L, 1000) {
            Timber.d("Chrono master")
            chronometerTicking()
        }

    }

    fun stopWorkout() {
        workoutIsDoneLiveData.postValue(true)
        timer?.apply {
            cancel()
            purge()
        }
        timer = null
        chronometerIsRunning = false
        chronometerIsRunningLiveData.postValue(chronometerIsRunning)
        elapsedTimeLiveData.postValue(0)
        currentTraining = -1
        countdownLiveData.postValue(-1)
        workoutProgressLiveData.postValue(currentTraining)
        currentTrainingLiveData.postValue(null)
        if (isHost.value == true && isInRoom.value == true) {
            sendWorkoutMessage(0, WorkoutMessageAction.Stop)
        }
    }

    private fun pauseWorkout() {
        if (chronometerIsRunning) {
            timeWhenPaused = SystemClock.elapsedRealtime()
            sendWorkoutMessage(-1, WorkoutMessageAction.Pause)
        } else {
            lastTrainingChange =
                SystemClock.elapsedRealtime() - (timeWhenPaused - lastTrainingChange)
            sendWorkoutMessage(-1, WorkoutMessageAction.Start)
        }
        chronometerIsRunning = !chronometerIsRunning
        chronometerIsRunningLiveData.postValue(chronometerIsRunning)
    }

    private fun chronometerTicking() {
        val elapsedTime = if (chronometerIsRunning) {
            ((SystemClock.elapsedRealtime() - lastTrainingChange) / 1000.0).roundToInt()
        } else {
            ((timeWhenPaused - lastTrainingChange) / 1000.0).roundToInt()
        }
        val endOfTraining = trainingCountdown - elapsedTime
        elapsedTimeLiveData.postValue(((SystemClock.elapsedRealtime() - timeWhenStarted) / 1000.0).roundToInt())
        countdownLiveData.postValue(endOfTraining)
        if (chronometerIsRunning) {
            sendWorkoutMessage(endOfTraining, WorkoutMessageAction.Tick)
        }
        if (endOfTraining <= 0) {
            if (currentTraining < expendedTrainings.count()) {
                if (expendedTrainings[currentTraining].trainingType != TrainingTypes.Repeated) {
                    skipTraining()
                }
            } else {
                stopWorkout()
            }
        }
    }

    fun skipTraining(): WorkoutError {
        if (isInRoom.value == true && isHost.value == false){
            return WorkoutError.NotHost
        }
        if (currentTraining < 0 && !chronometerIsRunning) {
            return WorkoutError.NoopError
        }
        if (currentTraining <= expendedTrainings.count() - 2) {
            currentTraining++
            trainingCountdown = expendedTrainings[currentTraining].duration
            lastTrainingChange = SystemClock.elapsedRealtime()
            workoutProgressLiveData.postValue(currentTraining)
            currentTrainingLiveData.postValue(expendedTrainings[currentTraining])
            countdownLiveData.postValue(expendedTrainings[currentTraining].duration)
            sendWorkoutMessage(
                expendedTrainings[currentTraining].duration,
                WorkoutMessageAction.TrainingStart
            )
        } else {
            stopWorkout()
        }
        return WorkoutError.NoopError
    }

    fun setTrainingList(trainings: List<Training>) {
        expendedTrainings = trainings
    }

    sealed class WorkoutError {
        object NotHost : WorkoutError()
        object NoopError : WorkoutError()
        object NoTraining : WorkoutError()
    }
}