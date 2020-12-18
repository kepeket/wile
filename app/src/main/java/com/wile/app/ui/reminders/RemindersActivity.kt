package com.wile.app.ui.reminders

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.wile.app.R
import com.wile.app.databinding.ActivityRemindersBinding
import com.wile.app.services.AlarmService
import com.wile.app.ui.main.TrainingListingActivity
import com.wile.app.ui.main.TrainingListingActivity_GeneratedInjector
import com.wile.core.databinding.DataBindingActivity
import com.wile.database.model.Reminder
import com.wile.database.model.ReminderDay
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class RemindersActivity : DataBindingActivity() {
    private val viewModel: RemindersViewModel by viewModels()
    private val binding: ActivityRemindersBinding by binding(R.layout.activity_reminders)
    private val timePickerDialogBuilder =
        MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H)

    private val dateFormatter = SimpleDateFormat("HH:mm", Locale.FRENCH)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.apply {
            viewModel = viewModel
        }

        setSupportActionBar(binding.mainToolbar.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val workoutId: Int = intent.getIntExtra(RemindersActivity.WORKOUT_ID, 0)

        binding.reminderTitle.text = getString(R.string.reminder_title, workoutId)

        binding.reminderHour.setOnClickListener {
            val timeParts = binding.reminderHour.text.split(':')
            timePickerDialogBuilder
                .setHour(timeParts[0].toInt())
                .setMinute(timeParts[1].toInt())
            val timePickerDialog = timePickerDialogBuilder
                .setTitleText(R.string.reminder_time_picker_title)
                .build()
            timePickerDialog.addOnPositiveButtonClickListener {
                binding.reminderHour.text =
                    getString(R.string.time_format, timePickerDialog.hour, timePickerDialog.minute)
                viewModel.updateTime(timePickerDialog.hour, timePickerDialog.minute)
            }
            timePickerDialog.show(supportFragmentManager, "fragment_tag")
        }

        val now = Calendar.getInstance().time
        binding.reminderHour.text = dateFormatter.format(now)


        viewModel.reminderLiveData.observe(this, { reminder ->
            reminder?.let {
                binding.reminderHour.text = getString(
                    R.string.time_format,
                    reminder.hour,
                    reminder.minute
                )
                binding.reminderMonday.isChecked = it.monday
                binding.reminderTuesday.isChecked = it.tuesday
                binding.reminderWednesday.isChecked = it.wednesday
                binding.reminderThursday.isChecked = it.thursday
                binding.reminderFriday.isChecked = it.friday
                binding.reminderSaturday.isChecked = it.saturday
                binding.reminderSunday.isChecked = it.sunday
                binding.reminderActive.isChecked = it.active

            }
        })

        binding.saveBtn.setOnClickListener {
            saveReminder()
        }

        listOf(
            ReminderDay.Monday,
            ReminderDay.Tuesday,
            ReminderDay.Wednesday,
            ReminderDay.Thursday,
            ReminderDay.Friday,
            ReminderDay.Saturday,
            ReminderDay.Sunday
        ).forEach { day ->
            val checkbox: MaterialCheckBox? =
                findViewById(resources.getIdentifier("reminder_${day.name}", "id", packageName))
            checkbox?.setOnCheckedChangeListener { _, checked ->
                viewModel.setDay(day, checked)
            }
        }

        binding.reminderActive.setOnCheckedChangeListener { _, checked ->
            if (checked){
                setResult(TrainingListingActivity.UPDATE_REMINDER_RESULT_TRUE)
            } else {
                setResult(TrainingListingActivity.UPDATE_REMINDER_RESULT_FALSE)
            }
            viewModel.setActive(checked)
        }
    }

    private fun saveReminder() {
        runBlocking {
            launch(Dispatchers.Default) {
                viewModel.saveReminder()
            }
        }
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.training_add_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.training_main_go -> {
            saveReminder()
            true
        }
        android.R.id.home -> {
            onBackPressed()
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val WORKOUT_ID = "workout_id"

        fun newIntent(context: Context) = Intent(context, RemindersActivity::class.java)

        fun showReminders(context: Context, workoutId: Int) = newIntent(context).apply {
            putExtra(WORKOUT_ID, workoutId)
        }
    }
}