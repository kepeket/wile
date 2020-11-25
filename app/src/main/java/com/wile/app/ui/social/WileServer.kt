package com.wile.app.ui.social

import com.squareup.moshi.JsonAdapter
import com.wile.app.model.Envelop
import com.wile.app.model.EnvelopRoom
import com.wile.app.model.RoomModels
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WileServer @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val envelopAdapter: JsonAdapter<Envelop>,
    private val listener: WebSocketListener
) {
    private var ws: WebSocket? = null

    fun connect(){
        val request: Request = Request.Builder().url(SERVER_URL).build()
        ws = okHttpClient.newWebSocket(request, listener)
    }

    fun disconnect() {
        ws?.close(1000, "bye")
    }

    fun joinRoom(roomPayload: RoomModels.RoomMessage): Boolean {
        ws?.let {
            val envelop = EnvelopRoom(roomPayload)
            return it.send(envelopAdapter.toJson(envelop))
        }
        return false
    }

    private companion object {
        const val SERVER_URL = "wss://24bc9af2f750.ngrok.io/chaussette"
    }
}
