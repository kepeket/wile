package com.wile.app.ui.social

import okhttp3.WebSocketListener

abstract class WileSocketListener: WebSocketListener() {
    abstract fun isConnected(): Boolean
}
