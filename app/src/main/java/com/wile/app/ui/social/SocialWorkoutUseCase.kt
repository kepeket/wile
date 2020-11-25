package com.wile.app.ui.social

import com.wile.app.model.EnvelopType
import com.wile.app.model.RoomMessageAction
import com.wile.app.model.RoomModels
import com.wile.app.model.WileMessage
import okhttp3.Response
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

// Todo : bad smell : a usecase should ideally be stateless and so not being a singleton.
@Singleton
class SocialWorkoutUseCase @Inject constructor(
    private val roomRepo: RoomRepo,
    private val listener: WileSocketListener
) {

    var inRoom = false
    var isHost = false
    var roomName = ""
    var userId = ""
    var members: HashMap<String, Boolean> = hashMapOf()

    fun setCallbacks(
        onOpen: (response: Response) -> Unit,
        onMessage: (type: EnvelopType, response: WileMessage) -> Unit,
        onClosed: (code: Int, reason: String) -> Unit,
        onFailure: (t: Throwable, response: Response?) -> Unit
    ) {
        // FixMe : you give an abstraction, to finally hardcast it to an implementation -> problem
        (listener as WileSocketListenerImpl).setCallbacks(
            onOpen = onOpen,
            onClosed = onClosed,
            onMessage = onMessage,
            onFailure = onFailure
        )
    }

    fun leaveRoom() {
        roomRepo.leaveRoom()
    }

    fun create(roomName: String, userId: String){
        this.userId = userId
        this.roomName = roomName
        isHost = true
        val message = RoomModels.RoomMessage(
            userId = userId,
            name = roomName,
            action = RoomMessageAction.Create
        )
        val ret = roomRepo.joinRoom(message)
        Timber.d("create %b", ret)
    }

    fun join(roomName: String, userId: String){
        this.userId = userId
        this.roomName = roomName
        isHost = false
        val message = RoomModels.RoomMessage(
            userId = userId,
            name = roomName,
            action = RoomMessageAction.Join
        )

        val ret = roomRepo.joinRoom(message)
        Timber.d("join %b", ret)
    }
}