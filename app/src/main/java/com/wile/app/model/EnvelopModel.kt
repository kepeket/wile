package com.wile.app.model

data class EnvelopModel(
    val type: EnvelopType,
    val message: WileMessage
)

sealed class EnvelopType {
    object Room: EnvelopType()
    object Ping: EnvelopType()
    object Pong: EnvelopType()
    object Error: EnvelopType()
}

