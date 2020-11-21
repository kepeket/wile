package com.wile.app.ui.social

import com.wile.app.model.EnvelopType
import com.wile.app.model.RoomMessageAction
import com.wile.app.model.RoomModels
import com.wile.app.model.WileMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Response
import okhttp3.WebSocketListener
import timber.log.Timber
import javax.inject.Inject

class SocialWorkoutUseCase @Inject constructor(
    val listener: WileSocketListener,
    val workoutController: SocialWorkoutController
    ) {

    fun setCallbacks(
        onOpen: (response: Response) -> Unit,
        onMessage: (type: EnvelopType, response: WileMessage) -> Unit,
        onClosed: (code: Int, reason: String) -> Unit,
        onFailure: (t: Throwable, response: Response?) -> Unit
    ) {
        (listener as WileSocketListenerImpl).setCallbacks(
            onOpen = onOpen,
            onClosed = onClosed,
            onMessage = onMessage,
            onFailure = onFailure
        )
    }

    fun connect(){
        workoutController.connect(listener as WebSocketListener)
    }

    fun disconnect(){
        workoutController.disconnect()
    }

    fun create(roomName: String, userId: String){
        val message = RoomModels.RoomMessage(
            userId = userId,
            name = roomName,
            action = RoomMessageAction.Create
        )
        val ret = workoutController.join(message)
        Timber.d("create %b", ret)
    }

    fun join(roomName: String, userId: String){
        val message = RoomModels.RoomMessage(
            userId = userId,
            name = roomName,
            action = RoomMessageAction.Join
        )

        val ret = workoutController.join(message)
        Timber.d("join %b", ret)
    }
}