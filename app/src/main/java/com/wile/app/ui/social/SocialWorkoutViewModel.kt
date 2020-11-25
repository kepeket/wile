package com.wile.app.ui.social

import androidx.databinding.ObservableBoolean
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.wile.app.base.LiveCoroutinesViewModel
import com.wile.app.model.*
import okhttp3.Response
import org.hashids.Hashids
import timber.log.Timber
import java.time.Instant

class SocialWorkoutViewModel @ViewModelInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    private val useCase: SocialWorkoutUseCase
) : LiveCoroutinesViewModel() {

    val roomNameCreate: MutableLiveData<String> = MutableLiveData("")
    val userName: MutableLiveData<String> = MutableLiveData("")
    val roomMembers: MutableLiveData<HashMap<String, Boolean>> = MutableLiveData(hashMapOf())
    val hosting: ObservableBoolean = ObservableBoolean(false)
    val isInRoom: MutableLiveData<Boolean> = MutableLiveData(false)
    private lateinit var callbackListener: WileSocketListenerCallback
    private var connected = false

    init {
        refreshConnectionStatus()
        useCase.setCallbacks(
            onOpen = ::onOpen,
            onClosed = ::onClosed,
            onMessage = ::onMessage,
            onFailure = ::onFailure
        )
    }

    fun saveState(){
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
         connected = useCase.isConnected()
    }

    fun setSocialWorkoutCallbackListener(listenerCallback: WileSocketListenerCallback){
        callbackListener = listenerCallback
    }

    private fun onOpen(response: Response) {
        connected = true
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
        connected = false
        setInRoom(false)
    }

    private fun onFailure(t: Throwable, response: Response?) {
        callbackListener.onConnectionFailure(t, response)
        connected = false
        setInRoom(false)
    }

    fun connect(){
        if (!connected) {
            useCase.connect()
        }
    }

    fun disconnect(){
        if (connected) {
            useCase.disconnect()
            connected = false
            setInRoom(false)
        }
    }

    fun create(user: String) {
        userName.value = user
        hosting.set(true)
        connect()
        useCase.create(roomNameCreate.value!!, user)
        setInRoom(true)
    }

    fun join(user: String, roomName: String) {
        userName.value = user
        roomNameCreate.value = roomName
        hosting.set(false)
        connect()
        useCase.join(roomName.toUpperCase(), user)
        setInRoom(true)
    }

    private fun setInRoom(bool: Boolean){
        useCase.inRoom = bool
        isInRoom.postValue(bool)
    }
    
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