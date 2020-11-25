package com.wile.app.ui.social

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WileServer @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val listener: WebSocketListener
) {
    var webSocket: WebSocket? = null

    fun connect() {
        webSocket = okHttpClient.newWebSocket(
                Request.Builder()
                        .url(SERVER_URL)
                        .build(),
                listener
        )
    }

    fun disconnect() {
        webSocket?.close(DISCONNECT_CODE, DISCONNECT_REASON)
        webSocket = null
    }

    private companion object {
        const val SERVER_URL = "wss://24bc9af2f750.ngrok.io/chaussette"
        const val DISCONNECT_REASON = "bye"
        const val DISCONNECT_CODE = 1000
    }
}
