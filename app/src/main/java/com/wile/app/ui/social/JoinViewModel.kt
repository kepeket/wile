package com.wile.app.ui.social

import androidx.annotation.WorkerThread
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.wile.app.base.LiveCoroutinesViewModel
import com.wile.app.model.*
import com.wile.database.model.Training
import com.wile.training.TrainingRepository
import okhttp3.Response
import org.hashids.Hashids
import timber.log.Timber
import java.time.Instant

class JoinViewModel @ViewModelInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    val useCase: SocialWorkoutUseCase
) : LiveCoroutinesViewModel(), WileSocketListener {

    val roomNameCreate: String
    val roomNameInput: MutableLiveData<String> = MutableLiveData("")
    val userName: LiveData<String>
    val roomMembers: MutableLiveData<HashMap<String, Boolean>> = MutableLiveData(hashMapOf())
    private lateinit var callbackListener: WileSocketListenerCallback
    private var connected = false


    init {
        userName = MutableLiveData(getRotatedUserName())
        val hashids = Hashids(userName.value)
        roomNameCreate = hashids.encode(Instant.now().toEpochMilli()/1000).toUpperCase().take(6)
        useCase.setCallbacks(
            onOpen = ::onOpen,
            onClosed = ::onClosed,
            onMessage = ::onMessage,
            onFailure = ::onFailure
        )
    }

    fun setSocialWorkoutCallbackListener(listenerCallback: WileSocketListenerCallback){
        callbackListener = listenerCallback
    }

    private fun onOpen(response: Response) {
        Timber.d(response.message())
        connected = true
        callbackListener.connectionOpen()
    }

    private fun onMessage(type: EnvelopType, response: WileMessage) {
        when(type){
            EnvelopType.Room -> {
                with(response as RoomModels.RoomMessage) {
                    when (response.action) {
                        RoomMessageAction.Joined -> {
                            roomMembers.value?.let { orig ->
                                roomMembers.postValue(orig.also{ it[response.userId] = true})
                            }
                            callbackListener.onUserJoined(response.userId, response.name)
                        }
                        RoomMessageAction.Created -> {
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
    }

    private fun onFailure(t: Throwable, response: Response?) {
        callbackListener.onConnectionFailure(t, response)
        connected = false
    }

    fun connect(){
        if (!connected) {
            useCase.connect()
        }
    }

    fun disconnect(){
        if (connected) {
            useCase.disconnect()
        }
    }

    fun create(): Boolean{
        userName.value?.let {
            useCase.create(roomNameCreate, it)
            return true
        }
        connect()
        return false
    }

    fun join(): Boolean {
        if (userName.value.isNullOrEmpty() ||
                roomNameInput.value.isNullOrEmpty()){
            return false
        }
        connect()
        useCase.join(roomNameInput.value!!.toUpperCase(), userName.value!!)
        return true
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