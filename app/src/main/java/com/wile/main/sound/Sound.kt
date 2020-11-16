package com.wile.main.sound

class Sound(
    val resId: Int,
    val priority: Int,
    val leftVolume: Float = 1f,
    val rightVolume: Float = 1f,
    val loop: Int = 0,
    val rate: Float = 1f
)
