package com.wile.app.ui.social

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.databinding.ObservableBoolean
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.switchMap
import com.wile.app.base.LiveCoroutinesViewModel
import com.wile.app.model.*
import com.wile.app.ui.workout.WorkoutService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.android.synthetic.main.workout_controller.view.*
import org.hashids.Hashids
import java.time.Instant

class SocialWorkoutViewModel @ViewModelInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context
) : LiveCoroutinesViewModel() {

    private var workoutService: WorkoutService? = null


    // Mediatords
    val roomNameCreate: MediatorLiveData<String> = MediatorLiveData()
    var roomName: MediatorLiveData<String> = MediatorLiveData()
    val userName: MediatorLiveData<String> = MediatorLiveData()
    val roomMembers: MediatorLiveData<HashMap<String, Boolean>> = MediatorLiveData()
    val isHost: MediatorLiveData<Boolean> = MediatorLiveData()
    val isInRoom: MediatorLiveData<Boolean> = MediatorLiveData()

    // Service binding callback
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as WorkoutService.WorkoutBinder
            workoutService = binder.getService()
            workoutService?.let { svc ->
                userName.addSource(svc.userName) {
                    if (it.isEmpty()){
                        userName.postValue(getRotatedUserName())
                    } else {
                        userName.postValue(it)
                    }
                }
                roomMembers.addSource(svc.roomMembers) {
                    roomMembers.postValue(it)
                }
                isHost.addSource(svc.isHost) {
                    isHost.postValue(it)
                }
                isInRoom.addSource(svc.isInRoom) {
                    isInRoom.postValue(it)
                }
                roomName.addSource(svc.roomName){
                    roomName.postValue(it)
                }
                roomNameCreate.addSource(svc.roomNameCreation){
                    if (it.isEmpty()){
                        roomNameCreate.postValue(
                            Hashids(userName.value).encode(Instant.now().toEpochMilli() / 1000).toUpperCase().take(6)
                        )
                    } else {
                        roomNameCreate.postValue(it)
                    }
                }
            }
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
        }
    }


    init {
        Intent(context, WorkoutService::class.java).also {  intent ->
            context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }


    fun create(user: String) {
        workoutService?.createRoom(user, roomNameCreate.value!!)
    }

    fun join(user: String, roomName: String) {
        workoutService?.joinRoom(user, roomName.toUpperCase())
    }

    fun leaveRoom() {
        workoutService?.leaveRoom()
    }

    /*fun saveState(){
        roomMembers.value?.let{
            useCase.members = it
        }
    }

     fun refreshConnectionStatus(){
        userName.value = if (useCase.userId.isNotEmpty()){
            useCase.userId
        } else {
            getRotatedUserName()
        }

        roomNameCreate.value = if (useCase.inRoom && useCase.roomName.isNotEmpty()) {
            useCase.roomName
        } else {
            Hashids(userName.value).encode(Instant.now().toEpochMilli() / 1000).toUpperCase().take(6)
        }

         if (roomMembers.value?.count() == 0 && useCase.members.count()>0){
             roomMembers.value = useCase.members
         }

         isInRoom.value = useCase.inRoom
         hosting.set(useCase.isHost)
    }

    fun setSocialWorkoutCallbackListener(listenerCallback: WileSocketListenerCallback){
        callbackListener = listenerCallback
    }

    private fun onOpen(response: Response) {
        callbackListener.connectionOpen()
    }

    private fun onMessage(type: EnvelopType, response: WileMessage) {
        when(type){
            EnvelopType.Room -> {
                with(response as RoomModels.RoomMessage) {
                    when (response.action) {
                        RoomMessageAction.Joined -> {
                            if (response.userId == userName.value){
                                setInRoom(true)
                            }
                            roomMembers.value?.let { orig ->
                                roomMembers.postValue(orig.also{ it[response.userId] = true})
                            }
                            callbackListener.onUserJoined(response.userId, response.name)
                        }
                        RoomMessageAction.Created -> {
                            setInRoom(true)
                            callbackListener.onRoomCreated(response.name)
                        }
                        RoomMessageAction.Left -> {
                            roomMembers.value?.let { orig ->
                                roomMembers.postValue(orig.also{ it.remove(response.userId) })
                            }
                            callbackListener.onUserLeft(response.userId, response.name)
                        }
                        else -> {}
                    }
                }
            }
            EnvelopType.Pong -> {
                with(response as PingModels.PongMessage) {
                    callbackListener.onPong(response.timecode)
                }
            }
            EnvelopType.Ping -> {
                with(response as PingModels.PingRequest) {
                    val ping = (Instant.now().toEpochMilli() - response.timecode) / 1000
                    Timber.d("time space between %s and me = %d", response.userId, ping)
                    val statusBool = ping <= 500
                    roomMembers.value?.let { orig ->
                        roomMembers.postValue(orig.also{ it[response.userId] = statusBool })
                    }
                }
            }
            EnvelopType.Error -> {
                callbackListener.onError((response as ErrorModels.Error).message)
            }
        }
    }

    private fun onClosed(code: Int, reason: String) {
        callbackListener.connectionClosed(code, reason)
        setInRoom(false)
    }

    private fun onFailure(t: Throwable, response: Response?) {
        callbackListener.onConnectionFailure(t, response)
        setInRoom(false)
    }

    fun onCancelSocialButtonClicked() {
        useCase.leaveRoom()
        setInRoom(false)
    }

    fun create(user: String) {
        userName.value = user
        hosting.set(true)
        useCase.create(roomNameCreate.value!!, user)
        setInRoom(true)
    }

    fun join(user: String, roomName: String) {
        userName.value = user
        roomNameCreate.value = roomName
        hosting.set(false)
        useCase.join(roomName.toUpperCase(), user)
        setInRoom(true)
    }

    private fun setInRoom(bool: Boolean){
        useCase.inRoom = bool
        isInRoom.postValue(bool)
    }*/
    
    private fun getRotatedUserName(): String {
        val fakeUserNames = listOf<String>(
                "RapidPanda",
                "RunningFrog",
                "BuffyPuppy",
                "EnergeticLama",
                "JumpyFox",
                "StretchyCat"
        )
        return fakeUserNames.random()
    }
}