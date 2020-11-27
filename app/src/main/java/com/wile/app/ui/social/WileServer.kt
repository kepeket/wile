package com.wile.app.ui.social

import com.tinder.scarlet.ws.Receive
import com.tinder.scarlet.ws.Send
import com.wile.app.model.*
import io.reactivex.Flowable
import okhttp3.WebSocket

interface WileServer {
    @Send
    fun messageRoom(join: EnvelopRoom)
    @Send
    fun pingServer(ping: EnvelopPing)
    @Send
    fun messageWorkout(ping: EnvelopWorkout)

    @Receive
    fun roomMessage(): Flowable<EnvelopRoom>
    @Receive
    fun workoutMessage(): Flowable<EnvelopWorkout>
    @Receive
    fun pingRequest(): Flowable<EnvelopPing>
    @Receive
    fun pongResponse(): Flowable<EnvelopPong>
    @Receive
    fun errorMessage(): Flowable<EnvelopError>

     companion object {
         //const val SERVER_URL = "wss://6fe4a7fb6de3.ngrok.io/chaussette"
         const val SERVER_URL = "wss://wile-workout.cleverapps.io/chaussette"
    }
}
