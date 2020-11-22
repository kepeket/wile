package com.wile.app.ui.social

import android.widget.Chronometer
import com.wile.app.model.RoomModels
import com.wile.app.ui.handler.WorkoutInterface
import okhttp3.WebSocketListener
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
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

    fun isConnected(): Boolean {
        return server.isConnected()
    }

    fun join(roomRequest: RoomModels.RoomMessage): Boolean{
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