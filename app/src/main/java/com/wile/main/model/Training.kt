package com.wile.main.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Training(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var workout: Int = 0,
    val name: String,
    val reps: Int = 0,
    val repRate: Int = 0,
    val duration: Int = 30,
    val sorting: Int
)