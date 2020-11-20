package com.wile.app.ui.social

import androidx.activity.viewModels
import com.wile.app.model.JoinRoomModels
import com.wile.training.TrainingRepository
import javax.inject.Inject

class SocialWorkoutUseCase {

    @Inject lateinit var workoutController: SocialWorkoutController

    fun connect(){
        workoutController.connect()
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