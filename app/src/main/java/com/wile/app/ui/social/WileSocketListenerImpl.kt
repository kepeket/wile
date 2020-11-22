package com.wile.app.ui.social

import com.squareup.moshi.Moshi
import com.wile.app.model.*
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import timber.log.Timber
import javax.inject.Inject

class WileSocketListenerImpl @Inject constructor(
    val moshi: Moshi
) : WileSocketListener, WebSocketListener() {

    private lateinit var onOpenCallback: (response: Response) -> Unit?
    private lateinit var onMessageCallback: (type: EnvelopType, response: WileMessage) -> Unit
    private lateinit var onConnectionClosedCallback: (code: Int, reason: String) -> Unit
    private lateinit var onFailureCallback: (t: Throwable, response: Response?) -> Unit


    fun setCallbacks(
        onOpen: (response: Response) -> Unit,
        onMessage: (type: EnvelopType, response: WileMessage) -> Unit,
        onClosed: (code: Int, reason: String) -> Unit,
        onFailure: (t: Throwable, response: Response?) -> Unit
    ) {
        onOpenCallback = onOpen
        onMessageCallback = onMessage
        onConnectionClosedCallback = onClosed
        onFailureCallback = onFailure
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        onOpenCallback(response)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        val env = moshi.adapter(Envelop::class.java).fromJson(text)

        when(env?.typeName){
            EnvelopType.Room -> {
                onMessageCallback(env.typeName, (env as EnvelopRoom).message)
            }
            EnvelopType.Ping -> {
                onMessageCallback(env.typeName, (env as EnvelopPing).message)
            }
            EnvelopType.Pong -> {
                onMessageCallback(env.typeName, (env as EnvelopPong).message)
            }
            EnvelopType.Error -> {
                onMessageCallback(env.typeName, (env as EnvelopError).message)
            }
            else -> onFailureCallback(Throwable("Unable to parse JSON"), null)
        }
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        TODO("Not yet implemented")
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        Timber.d("Closing the room")
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        onConnectionClosedCallback(code, reason)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        onFailureCallback(t, response)
    }
}