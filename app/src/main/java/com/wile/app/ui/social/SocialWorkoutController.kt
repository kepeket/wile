package com.wile.app.ui.social

import android.widget.Chronometer
import com.wile.app.model.JoinRoomModels
import com.wile.app.ui.handler.WorkoutInterface
import okhttp3.WebSocketListener
import javax.inject.Inject

class SocialWorkoutController @Inject constructor(
    val server: WileServer
) : WorkoutInterface {

    override var chronometerIsRunning: Boolean = false

    fun connect(listener: WebSocketListener){
        server.connect(listener)
    }

    fun disconnect(){
        server.disconnect()
    }

    fun join(roomRequest: JoinRoomModels.JoinRoomRequest): Boolean{
        return server.joinRoom(roomRequest)
    }

    override fun startStopWorkout() {
        TODO("Not yet implemented")
    }

    override fun startWorkout() {
        TODO("Not yet implemented")
    }

    override fun stopWorkout() {
        TODO("Not yet implemented")
    }

    override fun pauseWorkout() {
        TODO("Not yet implemented")
    }

    override fun skipTraining() {
        TODO("Not yet implemented")
    }

    override fun chronometerTicking(chronometer: Chronometer) {
        TODO("Not yet implemented")
    }

}