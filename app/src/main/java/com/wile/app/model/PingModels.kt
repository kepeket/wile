package com.wile.app.model

import com.squareup.moshi.JsonClass

class PingModels {
    @JsonClass(generateAdapter = true)
    data class PingRequest(
        val timecode: Long,
        val room: String,
        val userId: String
    ) : WileMessage

    @JsonClass(generateAdapter = true)
    data class PongMessage(
        val timecode: Long
    ) : WileMessage
}