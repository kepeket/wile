package com.wile.app.model

class PingModels {
    data class PingRequest(
        val timecode: Long,
        val room: String,
        val userId: String
    ) : WileMessage

    data class PongMessage(
        val timecode: Long
    ) : WileMessage
}