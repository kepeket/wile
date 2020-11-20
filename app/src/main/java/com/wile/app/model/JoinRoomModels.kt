package com.wile.app.model

class JoinRoomModels {
    data class JoinRoomRequest(
        val userId: String,
        val name: String
    ) : WileMessage

    data class JoinRoomMessage(
        val userId: String,
        val name: String,
        val action: String
    ) : WileMessage
}