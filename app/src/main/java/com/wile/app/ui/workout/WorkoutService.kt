package com.wile.app.ui.workout

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
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
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap
import kotlin.concurrent.fixedRateTimer
import kotlin.math.roundToInt
import kotlin.reflect.KParameter

@AndroidEntryPoint
class WorkoutService: Service() {

    @Inject
    lateinit var server: WileServer
    @Inject
    lateinit var scarletLifecycleRegistry: LifecycleRegistry

    private val binder = WorkoutBinder()

    private lateinit var expendedTrainings: List<Training>
    private var currentTraining = -1
    private var trainingCountdown = 0
    private var lastTrainingChange = 0L
    private var timeWhenPaused = 0L
    private var timeWhenStarted = 0L
    private var chronometerIsRunning = false
    private lateinit var timer: Timer

    val countdownLiveData: MutableLiveData<Int> = MutableLiveData(-1)
    val elapsedTimeLiveData: MutableLiveData<Int> = MutableLiveData(0)
    val workoutProgressLiveData: MutableLiveData<Int> = MutableLiveData(currentTraining)
    val currentTrainingLiveData: MutableLiveData<Training> = MutableLiveData()
    val chronometerIsRunningLiveData: MutableLiveData<Boolean> = MutableLiveData(chronometerIsRunning)
    val workoutIsDoneLiveData: MutableLiveData<Boolean> = MutableLiveData(false)

    // social LiveData
    val roomNameCreation: MutableLiveData<String> = MutableLiveData("")
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
     * Empty list of member when disconnected
     * Display actual room in bottomsheet (not the one we create)
     * CreatedRoomID refreshes when disconnecting
     */

    @SuppressLint("CheckResult")
    override fun onBind(p0: Intent?): IBinder? {
        scarletLifecycleRegistry.onNext(Lifecycle.State.Started)
        server.roomMessage().subscribe { it ->
            roomMessageReceived(it.message)
        }
        server.workoutMessage().subscribe { it ->
            workoutMessageReceived(it.message)
        }
        return binder
    }

    inner class WorkoutBinder : Binder() {
        fun getService(): WorkoutService = this@WorkoutService
    }

    // Social region

    fun createRoom(userId: String, room: String) {
        roomNameCreation.postValue(room)
        isHost.postValue(true)
        roomSubscription(userId, room, RoomMessageAction.Create)
    }


    fun joinRoom(userId: String, room: String){
        roomSubscription(userId, room, RoomMessageAction.Join)
    }

    fun leaveRoom() {
        isInRoom.value?.let { inRoom ->
            if (inRoom) {
                isInRoom.postValue(false)
                isHost.postValue(false)
                roomName.postValue("")
                roomNameCreation.postValue("")
                roomSubscription(userName.value!!, roomName.value!!, RoomMessageAction.Leave)
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
        when (response.action){
            WorkoutMessageAction.Started -> {
                // We are only interrested if we are not the host
                if (isHost.value?.equals(false) == true) {
                    chronometerIsRunningLiveData.postValue(true)
                }
            }
            WorkoutMessageAction.Stopped -> TODO()
            WorkoutMessageAction.Lobby -> {
                // Asking to put yourself in lobby, so we'll launch the workout activity in social mode
                startActivity(WorkoutActivity.startSocialWorkout(applicationContext, isHost.value!!))
            }
            WorkoutMessageAction.Ready -> {
                // People sending you ready only matters if you are a host
                if (isHost.value?.equals(true) == true){
                    readyMembers.value?.let{ members ->
                        if (!members.contains(response.userId)){
                            members.add(response.userId)
                        }
                        if (members.count() == roomMembers.value?.count()){
                            canStart.postValue(true)
                        }
                    }
                }
            }
            WorkoutMessageAction.TrainingStart -> {
                // Reset all livedata on the new joined training
                currentTrainingLiveData.postValue(response.training)
                countdownLiveData.postValue(response.countdown)
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
                }
                roomMembers.value?.let { orig ->
                    roomMembers.postValue(orig.also { it[response.userId] = true })
                }
                //callbackListener.onUserJoined(response.userId, response.name)
            }
            RoomMessageAction.Created -> {
                isInRoom.postValue(true)
                //callbackListener.onRoomCreated(response.name)
            }
            RoomMessageAction.Left -> {
                roomMembers.value?.let { orig ->
                    roomMembers.postValue(orig.also { it.remove(response.userId) })
                }
                //callbackListener.onUserLeft(response.userId, response.name)
            }
            else -> {
            }
        }
    }


    // Private region
    fun startStopWorkout(): Boolean {
        if (expendedTrainings.count() > 1) {
            if (currentTraining < 0) {
                startWorkout()
            } else {
                pauseWorkout()
            }
            return true
        }
        return false
    }

    private fun startWorkout() {
        currentTraining = -1
        workoutProgressLiveData.postValue(currentTraining)
        workoutIsDoneLiveData.postValue(false)
        chronometerIsRunning = true
        skipTraining()
        chronometerIsRunningLiveData.postValue(chronometerIsRunning)
        timeWhenStarted = SystemClock.elapsedRealtime()
        timer = fixedRateTimer("timer", false, 0L, 1000) {
            chronometerTicking()
        }

    }

    fun stopWorkout() {
        if (chronometerIsRunning) {
            timer.cancel()
            chronometerIsRunning = false
            chronometerIsRunningLiveData.postValue(chronometerIsRunning)
            currentTraining = -1
            workoutProgressLiveData.postValue(currentTraining)
        }
        workoutIsDoneLiveData.postValue(true)
    }

    private fun pauseWorkout() {
        if (chronometerIsRunning) {
            timeWhenPaused = SystemClock.elapsedRealtime()
        } else {
            lastTrainingChange = SystemClock.elapsedRealtime() - (timeWhenPaused - lastTrainingChange)
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
        elapsedTimeLiveData.postValue(((SystemClock.elapsedRealtime() - timeWhenStarted)/1000.0).roundToInt())
        countdownLiveData.postValue(endOfTraining)
        if (endOfTraining <= 0) {
            if (expendedTrainings[currentTraining].trainingType != TrainingTypes.Repeated) {
                skipTraining()
            }
        }
    }

    fun skipTraining() {
        if (currentTraining < 0 && !chronometerIsRunning) {
            return
        }
        currentTraining++
        workoutProgressLiveData.postValue(currentTraining)
        currentTrainingLiveData.postValue(expendedTrainings[currentTraining])
        countdownLiveData.postValue(expendedTrainings[currentTraining].duration)
        if (currentTraining <= expendedTrainings.count() - 1) {
            trainingCountdown = expendedTrainings[currentTraining].duration
            lastTrainingChange = SystemClock.elapsedRealtime()
        } else {
            stopWorkout()
        }
    }

    fun setTrainingList(trainings: List<Training>) {
        expendedTrainings = trainings
    }
}