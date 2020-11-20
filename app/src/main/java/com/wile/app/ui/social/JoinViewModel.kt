package com.wile.app.ui.social

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.wile.app.base.LiveCoroutinesViewModel
import com.wile.database.model.Training
import com.wile.training.TrainingRepository
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import org.hashids.Hashids
import java.time.Instant
import javax.inject.Inject

class JoinViewModel @ViewModelInject constructor(
    private val trainingRepository: TrainingRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : LiveCoroutinesViewModel() {

    lateinit var useCase: SocialWorkoutUseCase
    val trainingListLiveData: MutableLiveData<List<Training>> = MutableLiveData()
    val trainingDurationLiveData: MutableLiveData<Int> = MutableLiveData(0)
    val roomNameCreate: String
    val roomNameInput: MutableLiveData<String> = MutableLiveData("")
    val userName: MutableLiveData<String> = MutableLiveData("")

    init {
        userName.value = getRotatedUserName()
        val hashids = Hashids(userName.value)
        roomNameCreate = hashids.encode(Instant.now().toEpochMilli()/1000)
    }

    fun setWebSocketListener(listener: WebSocketListener) {
        useCase = SocialWorkoutUseCase(listener)
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