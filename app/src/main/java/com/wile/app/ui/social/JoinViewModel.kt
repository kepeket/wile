package com.wile.app.ui.social

import android.widget.Toast
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.wile.app.R
import com.wile.app.base.LiveCoroutinesViewModel
import com.wile.app.model.*
import com.wile.database.model.Training
import com.wile.training.TrainingRepository
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import org.hashids.Hashids
import timber.log.Timber
import java.time.Instant
import javax.inject.Inject

class JoinViewModel @ViewModelInject constructor(
    private val trainingRepository: TrainingRepository,
    @Assisted private val savedStateHandle: SavedStateHandle,
    val workoutController: SocialWorkoutController
) : LiveCoroutinesViewModel(), WileSocketListener {

    private val listenerImpl = WileSocketListenerImpl(
        onOpen = ::onOpen,
        onClosed = ::onClosed,
        onMessage = ::onMessage,
        onFailure = ::onFailure,
    )
    private var useCase = SocialWorkoutUseCase(listenerImpl, workoutController)
    val trainingListLiveData: MutableLiveData<List<Training>> = MutableLiveData()
    val trainingDurationLiveData: MutableLiveData<Int> = MutableLiveData(0)
    val roomNameCreate: String
    val roomNameInput: MutableLiveData<String> = MutableLiveData("")
    val userName: MutableLiveData<String> = MutableLiveData("")
    val roomMembers: MutableLiveData<HashMap<String, Boolean>> = MutableLiveData()
    private lateinit var callbackListener: WileSocketListenerCallback


    init {
        userName.value = getRotatedUserName()
        val hashids = Hashids(userName.value)
        roomNameCreate = hashids.encode(Instant.now().toEpochMilli()/1000)
        roomMembers.value = hashMapOf()
    }

    fun setSocialWorkoutCallbackListener(listenerCallback: WileSocketListenerCallback){
        callbackListener = listenerCallback
    }

    private fun onOpen(response: Response) {
        Timber.d(response.message())
        callbackListener.connectionOpen()
    }

    private fun onMessage(type: EnvelopType, response: WileMessage) {
        when(type){
            EnvelopType.Room -> {
                with(response as JoinRoomModels.JoinRoomMessage) {
                    when (response.action) {
                        RoomMessageAction.Joined -> {
                            roomMembers.value?.set(response.userId, true)
                            callbackListener.onUserJoined(response.userId, response.name)
                        }
                        RoomMessageAction.Created -> {
                            callbackListener.onRoomCreated(response.name)
                        }
                        RoomMessageAction.Left -> {
                            roomMembers.value?.remove(response.userId)
                            callbackListener.onUserLeft(response.userId, response.name)
                        }
                    }
                }
            }
            EnvelopType.Pong -> {
                with(response as PingModels.PongMessage) {
                    callbackListener.onPong(response.timecode)
                }
            }
            EnvelopType.Ping -> {
                val now = Instant.now().toEpochMilli()
                with(response as PingModels.PingRequest) {
                    Timber.d("time space between %s and me = %d", response.userId, now - response.timecode)
                }
            }
            EnvelopType.Error -> {
                callbackListener.onError((response as ErrorModels.Error).message)
            }
        }
    }

    private fun onClosed(code: Int, reason: String) {
        callbackListener.connectionClosed(code, reason)
    }

    private fun onFailure(t: Throwable, response: Response?) {
        callbackListener.onConnectionFailure(t, response)
    }

    fun connect(){
        useCase.connect()
    }

    fun disconnect(){
        useCase.disconnect()
    }

    fun create(): Boolean{
        userName.value?.let {
            useCase.join(roomNameCreate, it)
            return true
        }
        return false
    }

    fun join(): Boolean {
        if (userName.value.isNullOrEmpty() ||
                roomNameInput.value.isNullOrEmpty()){
            return false
        }
        useCase.join(roomNameInput.value!!, userName.value!!)
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