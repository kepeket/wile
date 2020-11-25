package com.wile.app.ui.social

import android.widget.Chronometer
import com.wile.app.model.RoomModels
import com.wile.app.ui.handler.WorkoutInterface
import okhttp3.WebSocketListener
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SocialWorkoutController @Inject constructor(
    private val server: WileServer
) {

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
}