package com.wile.main.binding

import android.view.View
import android.widget.Chronometer
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.databinding.BindingAdapter
import com.wile.main.R
import com.wile.main.ui.main.WorkoutInterface

object ViewBinding {

    @JvmStatic
    @BindingAdapter("toast")
    fun bindToast(view: View, text: String?) {
        text?.let {
            if (it.isNotEmpty()) {
                Toast.makeText(view.context, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    @JvmStatic
    @BindingAdapter("gone")
    fun bindGone(view: View, shouldBeGone: Boolean) {
        view.visibility = if (shouldBeGone) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    @JvmStatic
    @BindingAdapter("onBackPressed")
    fun bindOnBackPressed(view: View, onBackPress: Boolean) {
        val context = view.context
        if (onBackPress && context is OnBackPressedDispatcherOwner) {
            view.setOnClickListener {
                context.onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    @JvmStatic
    @BindingAdapter("workoutController")
    fun bindWorkoutController(view: View, controller: WorkoutInterface) {
        val stop = view.findViewById<ImageButton>(R.id.stop)
        val pause = view.findViewById<ImageButton>(R.id.pause)
        val skip = view.findViewById<ImageButton>(R.id.next)
        stop?.setOnClickListener {
            controller.stopWorkout()
        }
        pause?.setOnClickListener {
            controller.pauseWorkout()
        }
        skip?.setOnClickListener {
            controller.skipTraining()
        }
    }

    @JvmStatic
    @BindingAdapter("ticker")
    fun bindChronometerTicker(view: Chronometer, ticker: WorkoutInterface) {
        view.setOnChronometerTickListener {
            ticker.chronometerTicking(it)
        }
    }
}