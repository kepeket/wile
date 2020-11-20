package com.wile.app.ui.social

import com.google.gson.Gson
import com.wile.app.model.EnvelopModel
import com.wile.app.model.EnvelopType
import com.wile.app.model.JoinRoomModels
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject

class WileServer @Inject constructor(
    val okHttpClient: OkHttpClient,
    val gson: Gson
) {
    private val serverUrl = "wss://24bc9af2f750.ngrok.io/chaussette"
    private var ws: WebSocket? = null

    fun connect(webSocketListener: WebSocketListener){
        val request: Request = Request.Builder().url(serverUrl).build()
        ws = okHttpClient.newWebSocket(request, webSocketListener)
    }

    fun disconnect(){
        okHttpClient.dispatcher().executorService().shutdown()
    }

    fun joinRoom(roomPayload: JoinRoomModels.JoinRoomRequest): Boolean {
        ws?.let {
            val envelop = EnvelopModel(EnvelopType.Room, roomPayload)
            return it.send(gson.toJson(envelop))
        }
        return false
    }
}