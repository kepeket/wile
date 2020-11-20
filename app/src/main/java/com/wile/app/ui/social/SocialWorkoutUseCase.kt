package com.wile.app.ui.social

import com.wile.app.model.JoinRoomModels
import okhttp3.WebSocketListener
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
            roomName,
            userId
        )
        workoutController.join(message)
    }
}