package com.wile.app.model

class JoinRoomModels {
    data class JoinRoomRequest(
        val userId: String,
        val name: String
    ) : WileMessage

    data class JoinRoomMessage(
        val userId: String,
        val name: String,
        val action: RoomMessageAction
    ) : WileMessage

    companion object {
        const val CREATED = "created"
        const val JOINED = "joined"
    }
}

sealed class RoomMessageAction {
    object Created: RoomMessageAction()
    object Joined: RoomMessageAction()
}
