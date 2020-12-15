package com.wile.database.model

import android.icu.util.Calendar
import androidx.databinding.BaseObservable
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date
import java.sql.Time
import kotlin.time.ExperimentalTime
import kotlin.time.hours
import kotlin.time.minutes

@Entity
data class Reminder(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var workout: Int = 0,
    var active: Boolean = false,
    var hour: Int = 0,
    var minute: Int = 0,
    var monday: Boolean = false,
    var tuesday: Boolean = false,
    var wednesday: Boolean = false,
    var thursday: Boolean = false,
    var friday: Boolean = false,
    var saturday: Boolean = false,
    var sunday: Boolean = false
) : BaseObservable()

sealed class ReminderDay(val name: String) {
    object Monday: ReminderDay("monday")
    object Tuesday: ReminderDay("tuesday")
    object Wednesday: ReminderDay("wednesday")
    object Thursday: ReminderDay("thursday")
    object Friday: ReminderDay("friday")
    object Saturday: ReminderDay("saturday")
    object Sunday: ReminderDay("sunday")
}
