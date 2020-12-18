package com.wile.app.ui.social

import android.content.*
import android.os.IBinder
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.SavedStateHandle
import com.wile.core.viewmodel.LiveCoroutinesViewModel
import com.wile.app.services.WorkoutService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlin.collections.HashMap
import kotlin.random.Random

class SocialWorkoutViewModel @ViewModelInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context,
    private val sharedPreferences: SharedPreferences
) : LiveCoroutinesViewModel() {

    private var workoutService: WorkoutService? = null

    // Mediators
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
                sharedPreferences.registerOnSharedPreferenceChangeListener { pref, value ->
                    if (pref.equals("custom_name")) {
                        if (value.isNotEmpty()) {
                            userName.postValue(value)
                        }
                    }
                }

                userName.addSource(svc.userName) {
                    if (it.isEmpty()) {
                        val prefUserName = sharedPreferences.getString("custom_name", "")
                        if (prefUserName?.isNotEmpty() == true) {
                            userName.postValue(prefUserName)
                        } else {
                            userName.postValue(getRotatedUserName())
                        }
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
                roomName.addSource(svc.roomName) {
                    roomName.postValue(it)
                }
            }
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
        }
    }


    init {
        Intent(context, WorkoutService::class.java).also { intent ->
            context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }


    fun create(user: String) {
        workoutService?.createRoom(user)
    }

    fun join(user: String, roomName: String) {
        workoutService?.joinRoom(user, roomName.toUpperCase())
    }

    fun leaveRoom() {
        workoutService?.leaveRoom()
    }

    /*
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
*/

    private fun getRotatedUserName(): String {
        val fakeUserNames = listOf(
            "RapidPanda",
            "RunningFrog",
            "BuffyPuppy",
            "EnergeticLama",
            "JumpyFox",
            "StretchyCat"
        )
        return String.format("%s-%s", fakeUserNames.random(), Random.nextLong(100, 999))
    }
}