package com.wile.app.model

import com.squareup.moshi.FromJson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.ToJson

@JsonClass(generateAdapter = true)
data class EnvelopRoom(
    @Json(name = "type") val type: EnvelopType = EnvelopType.Room,
    val message: RoomModels.RoomMessage
): Envelop()

@JsonClass(generateAdapter = true)
data class EnvelopPing(
    val type: EnvelopType = EnvelopType.Ping,
    val message: PingModels.PingRequest
): Envelop()

@JsonClass(generateAdapter = true)
data class EnvelopPong(
    val type: EnvelopType = EnvelopType.Pong,
    val message: PingModels.PingRequest
): Envelop()

@JsonClass(generateAdapter = true)
data class EnvelopError(
    val type: EnvelopType = EnvelopType.Error,
    val message: ErrorModels.Error
): Envelop()


sealed class Envelop

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