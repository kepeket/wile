package com.wile.app.ui.social

import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

class WileSocketListenerImpl(
    val onOpen: (response: Response) -> Unit,
    val onMessage: (text: String) -> Unit,
    val onClosed: (code: Int, reason: String) -> Unit,
    val onFailure : (t: Throwable, response: Response?) -> Unit
) : WileSocketListener, WebSocketListener() {
    override fun onOpen(webSocket: WebSocket, response: Response) {
       onOpen(response)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        onMessage(text)
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