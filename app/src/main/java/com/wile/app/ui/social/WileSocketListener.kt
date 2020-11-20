package com.wile.app.ui.social

import okhttp3.Response
import okhttp3.WebSocket
import okio.ByteString

interface WileSocketListener {
    fun onOpen(webSocket: WebSocket, response: Response) {}

    fun onMessage(webSocket: WebSocket, text: String) {}

    fun onMessage(webSocket: WebSocket, bytes: ByteString) {}

    fun onClosing(webSocket: WebSocket, code: Int, reason: String) {}

    fun onClosed(webSocket: WebSocket, code: Int, reason: String) {}

    fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {}
}