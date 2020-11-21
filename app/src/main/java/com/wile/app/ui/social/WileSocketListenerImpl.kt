package com.wile.app.ui.social

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.squareup.moshi.Moshi
import com.wile.app.model.*
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

class WileSocketListenerImpl constructor(
    val onOpen: (response: Response) -> Unit,
    val onMessage: (type: EnvelopType, response: WileMessage) -> Unit,
    val onClosed: (code: Int, reason: String) -> Unit,
    val onFailure: (t: Throwable, response: Response?) -> Unit
) : WileSocketListener, WebSocketListener() {

    // FixMe: Unable to inject
    private val moshi = Moshi.Builder()
        .add(EnvelopTypeAdapter())
        .add(RoomActionAdapter())
        .build()
    private val envelopJSONAdapter = moshi.adapter(EnvelopModel::class.java)



    override fun onOpen(webSocket: WebSocket, response: Response) {
       onOpen(response)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        val env:EnvelopModel? = envelopJSONAdapter.fromJson(text)

        when(env?.type){
            EnvelopType.Room -> {
                onMessage(env.type, env.message)
            }
            EnvelopType.Ping -> {
                onMessage(env.type, env.message)
            }
            EnvelopType.Pong -> {
                onMessage(env.type, env.message)
            }
            EnvelopType.Error -> {
                onMessage(env.type, env.message)
            }
        }
        onFailure(Throwable("Unable to parse JSON"), null)
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        TODO("Not yet implemented")
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        TODO("Not yet implemented")
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        onClosed(code, reason)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        onFailure(t, response)
    }
}