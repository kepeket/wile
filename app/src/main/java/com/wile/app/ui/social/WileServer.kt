package com.wile.app.ui.social

import com.squareup.moshi.Moshi
import com.wile.app.model.Envelop
import com.wile.app.model.EnvelopRoom
import com.wile.app.model.RoomModels
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject

class WileServer @Inject constructor(
    val okHttpClient: OkHttpClient,
    val moshi: Moshi
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

    fun joinRoom(roomPayload: RoomModels.RoomMessage): Boolean {
        ws?.let {
            val envelop = EnvelopRoom(roomPayload)
            return it.send(moshi.adapter(Envelop::class.java).toJson(envelop))
        }
        return false
    }
}