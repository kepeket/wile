package com.wile.app.ui.social

import com.google.gson.Gson
import com.wile.app.model.EnvelopModel
import com.wile.app.model.JoinRoomModels
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import javax.inject.Inject

class WileServer{
    @Inject
    lateinit var okHttpClient: OkHttpClient
    @Inject
    lateinit var webSocketListener: WileSocketListener
    @Inject
    lateinit var gson: Gson

    private val serverUrl = "ws://24bc9af2f750.ngrok.io"
    private var ws: WebSocket? = null

    fun connect(){
        val request: Request = Request.Builder().url(serverUrl).build()
        ws = okHttpClient.newWebSocket(request, webSocketListener)
    }

    fun disconnect(){
        okHttpClient.dispatcher().executorService().shutdown()
    }

    fun joinRoom(roomPayload: JoinRoomModels.JoinRoomRequest): Boolean {
        ws?.let {
            val envelop = EnvelopModel("room", roomPayload)
            it.send(gson.toJson(envelop))
            return true
        }
        return false
    }
}