package com.wile.app.ui.social

import com.squareup.moshi.JsonAdapter
import com.wile.app.model.Envelop
import com.wile.app.model.EnvelopRoom
import com.wile.app.model.RoomModels
import javax.inject.Inject

class RoomRepo @Inject constructor(
        private val envelopAdapter: JsonAdapter<Envelop>,
        private val wileServer: WileServer
) {
    fun joinRoom(roomPayload: RoomModels.RoomMessage): Boolean {
        // Todo : could be done by a custom getter on webSocket var
        if (wileServer.webSocket == null) {
            wileServer.connect()
        }

        wileServer.webSocket?.let {
            val envelop = EnvelopRoom(roomPayload)
            return it.send(envelopAdapter.toJson(envelop))
        }
        return false
    }

    fun leaveRoom() {
        wileServer.disconnect()
    }
}
