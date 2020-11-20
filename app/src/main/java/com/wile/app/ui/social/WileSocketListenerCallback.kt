package com.wile.app.ui.social

import com.google.gson.Gson
import com.wile.app.model.EnvelopModel
import com.wile.app.model.EnvelopType
import com.wile.app.model.WileMessage
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import javax.inject.Inject

class WileSocketListenerCallback(
    val connectionOpen: () -> Unit,
    val connectionClosed: (code: Int, reason: String) -> Unit,
    val onConnectionFailure: (t: Throwable, response: Response?) -> Unit,
    val onRoomCreated: (name: String) -> Unit,
    val onUserJoined: (userId: String, room: String) -> Unit,
    val onUserLeft: (userId: String, room: String) -> Unit,
    val onPong: (time: Long) -> Unit
)