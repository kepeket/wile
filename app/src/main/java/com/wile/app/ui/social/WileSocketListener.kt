package com.wile.app.ui.social

import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import timber.log.Timber

class WileSocketListener : WebSocketListener() {
    override fun onOpen(webSocket: WebSocket, response: Response) {
        Timber.d("Open WS: %s", response.body().toString())
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        Timber.d("Closed WS: %s", reason.toString())
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason)
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        super.onMessage(webSocket, bytes)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        Timber.d("Message WS: %s", text)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Timber.d("Failure WS: %s", response?.body().toString())
    }
}