package com.wile.app.model

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonClass
import com.squareup.moshi.ToJson

class RoomModels {
    @JsonClass(generateAdapter = true)
    data class RoomMessage(
        val userId: String,
        val name: String,
        val action: RoomMessageAction
    ) : WileMessage
}

sealed class RoomMessageAction {
    object Create: RoomMessageAction()
    object Created: RoomMessageAction()
    object Join: RoomMessageAction()
    object Joined: RoomMessageAction()
    object Left: RoomMessageAction()
    object Leave: RoomMessageAction()
}

const val ROOM_ACTION_CREATED = "created"
const val ROOM_ACTION_CREATE = "create"
const val ROOM_ACTION_JOINED = "joined"
const val ROOM_ACTION_JOIN = "join"
const val ROOM_ACTION_LEAVE = "leave"
const val ROOM_ACTION_LEFT = "left"


class RoomActionAdapter {
    @FromJson
    fun fromJson(action: String): RoomMessageAction = when (action) {
        ROOM_ACTION_CREATED -> RoomMessageAction.Created
        ROOM_ACTION_JOINED -> RoomMessageAction.Joined
        ROOM_ACTION_CREATE -> RoomMessageAction.Create
        ROOM_ACTION_JOIN -> RoomMessageAction.Join
        ROOM_ACTION_LEFT -> RoomMessageAction.Left
        ROOM_ACTION_LEAVE -> RoomMessageAction.Leave
        else -> throw RuntimeException("Not support data type")
    }

    @ToJson
    fun toJson(roomAction: RoomMessageAction): String = when (roomAction) {
        RoomMessageAction.Created -> ROOM_ACTION_CREATED
        RoomMessageAction.Joined -> ROOM_ACTION_JOINED
        RoomMessageAction.Left -> ROOM_ACTION_LEFT
        RoomMessageAction.Create -> ROOM_ACTION_CREATE
        RoomMessageAction.Join -> ROOM_ACTION_JOIN
        RoomMessageAction.Leave -> ROOM_ACTION_LEAVE
    }
}