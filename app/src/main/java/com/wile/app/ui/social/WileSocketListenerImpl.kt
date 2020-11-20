package com.wile.app.ui.social

import com.google.gson.Gson
import com.wile.app.model.EnvelopModel
import com.wile.app.model.EnvelopType
import com.wile.app.model.WileMessage
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

class WileSocketListenerImpl constructor(
    val onOpen: (response: Response) -> Unit,
    val onMessage: (type: EnvelopType, response: WileMessage) -> Unit,
    val onClosed: (code: Int, reason: String) -> Unit,
    val onFailure : (t: Throwable, response: Response?) -> Unit
) : WileSocketListener, WebSocketListener() {

    // FixMe: Unable to inject
    private var gson = Gson()


    override fun onOpen(webSocket: WebSocket, response: Response) {
       onOpen(response)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        val env:EnvelopModel = gson.fromJson(text, EnvelopModel::class.java)

        when(env.type){
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