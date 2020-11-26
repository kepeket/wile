package com.wile.app.model

import com.squareup.moshi.FromJson
import com.squareup.moshi.Json
import com.squareup.moshi.ToJson

data class EnvelopRoom(
    val type: EnvelopType = EnvelopType.Room,
    val message: RoomModels.RoomMessage
): Envelop(EnvelopType.Room)
data class EnvelopPing(
    val message: PingModels.PingRequest
): Envelop(EnvelopType.Ping)
data class EnvelopPong(
    val message: PingModels.PingRequest
): Envelop(EnvelopType.Pong)
data class EnvelopError(
    val message: ErrorModels.Error
): Envelop(EnvelopType.Error)


sealed class Envelop(@Json(name = "type") val typeName: EnvelopType)

sealed class EnvelopType {
    object Room: EnvelopType()
    object Ping: EnvelopType()
    object Pong: EnvelopType()
    object Error: EnvelopType()
}

const val ENVELOP_TYPE_ROOM = "room"
const val ENVELOP_TYPE_PING = "ping"
const val ENVELOP_TYPE_PONG = "pong"
const val ENVELOP_TYPE_ERROR = "error"

class EnvelopTypeAdapter {
    @FromJson
    fun fromJson(typeName: String): EnvelopType = when (typeName) {
        ENVELOP_TYPE_ROOM -> EnvelopType.Room
        ENVELOP_TYPE_PING -> EnvelopType.Ping
        ENVELOP_TYPE_PONG -> EnvelopType.Pong
        ENVELOP_TYPE_ERROR -> EnvelopType.Error
        else -> throw RuntimeException("Not support data type")
    }

    @ToJson
    fun toJson(envelopType: EnvelopType): String = when (envelopType) {
        EnvelopType.Room -> ENVELOP_TYPE_ROOM
        EnvelopType.Ping -> ENVELOP_TYPE_PING
        EnvelopType.Pong -> ENVELOP_TYPE_PONG
        EnvelopType.Error -> ENVELOP_TYPE_ERROR
    }
}