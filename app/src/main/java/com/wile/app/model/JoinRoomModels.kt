package com.wile.app.model

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

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
}

sealed class RoomMessageAction {
    object Created: RoomMessageAction()
    object Joined: RoomMessageAction()
    object Left: RoomMessageAction()
}

const val ROOM_ACTION_CREATED = "created"
const val ROOM_ACTION_JOINED = "joined"
const val ROOM_ACTION_LEFT = "left"


class RoomActionAdapter {
    @FromJson
    fun fromJson(action: String): RoomMessageAction = when (action) {
        ROOM_ACTION_CREATED -> RoomMessageAction.Created
        ROOM_ACTION_JOINED -> RoomMessageAction.Joined
        ROOM_ACTION_LEFT -> RoomMessageAction.Left
        else -> throw RuntimeException("Not support data type")
    }

    @ToJson
    fun toJson(roomAction: RoomMessageAction): String = when (roomAction) {
        RoomMessageAction.Created -> ROOM_ACTION_CREATED
        RoomMessageAction.Joined -> ROOM_ACTION_JOINED
        RoomMessageAction.Left -> ROOM_ACTION_LEFT
    }
}