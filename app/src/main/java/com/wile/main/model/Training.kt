package com.wile.main.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Training(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var workout: Int = 0,
    var name: String,
    var reps: Int = 0,
    var repRate: Int = 0,
    var duration: Int = 30,
    var sorting: Int
)