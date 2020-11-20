package com.wile.app.ui.social

import com.wile.app.model.JoinRoomModels
import okhttp3.WebSocketListener
import timber.log.Timber
import javax.inject.Inject

class SocialWorkoutUseCase @Inject constructor(
    val listener: WebSocketListener,
    val workoutController: SocialWorkoutController
    ) {

    fun connect(){
        workoutController.connect(listener)
    }

    fun disconnect(){
        workoutController.disconnect()
    }

    fun join(roomName: String, userId: String){
        val message = JoinRoomModels.JoinRoomRequest(
            userId = userId,
            name = roomName
        )
        val ret = workoutController.join(message)
        Timber.d("join %b", ret)
    }
}