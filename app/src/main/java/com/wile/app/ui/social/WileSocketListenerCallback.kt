package com.wile.app.ui.social

import okhttp3.Response

class WileSocketListenerCallback(
    val connectionOpen: () -> Unit,
    val connectionClosed: (code: Int, reason: String) -> Unit,
    val onConnectionFailure: (t: Throwable, response: Response?) -> Unit,
    val onRoomCreated: (name: String) -> Unit,
    val onUserJoined: (userId: String, room: String) -> Unit,
    val onUserLeft: (userId: String, room: String) -> Unit,
    val onPong: (time: Long) -> Unit,
    val onError: (message: String) -> Unit
)