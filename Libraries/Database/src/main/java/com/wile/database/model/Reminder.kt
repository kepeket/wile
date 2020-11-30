package com.wile.database.model

import androidx.databinding.BaseObservable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Reminder(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var workout: Int = 0,
    var active: Boolean = false
) : BaseObservable()
