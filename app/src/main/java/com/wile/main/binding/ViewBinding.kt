package com.wile.main.binding

import android.annotation.SuppressLint
import android.view.View
import android.widget.Chronometer
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.databinding.BindingAdapter
import com.wile.main.R
import com.wile.main.ui.main.WorkoutInterface

object ViewBinding {

    @JvmStatic
    @BindingAdapter("android:text")
    fun bindTextValue(view: TextView, text: String?){
        text?.let {
            view.text = text
        }
    }

    @JvmStatic
    @BindingAdapter("android:text")
    fun bindIntValue(view: TextView, value: Int) {
        view.text = value.toString()
    }

    @SuppressLint("ClickableViewAccessibility")
    @JvmStatic
    @BindingAdapter(value = ["counterTarget", "counterDirection"], requireAll = true)
    fun bindCounterBtn(view: ImageButton, counterTarget: TextView, counterDirection: String?) {
        val lambda = fun(){
            counterTarget.let { textview ->
                val stringNumber = textview.text.toString()
                var curValue = if (stringNumber.isNotBlank()) stringNumber.toInt() else 0
                if (counterDirection == "sub"){
                    curValue--
                } else {
                    curValue++
                }
                textview.text = curValue.toString()
            }
        }
        view.setOnClickListener {
            lambda()
        }
        view.setOnTouchListener(RepeatListener(
                initialInterval = 400, initialRepeatDelay = 100
        ) { lambda() }
        )
    }

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
        val chronometer = view.findViewById<Chronometer>(R.id.chronometer)
        controller.setBottomSheetView(view)
        stop?.setOnClickListener {
            controller.stopWorkout()
        }
        pause?.setOnClickListener {
            controller.pauseWorkout()
        }
        skip?.setOnClickListener {
            controller.skipTraining()
        }
        chronometer?.setOnChronometerTickListener {
            controller.chronometerTicking(it)
        }
    }
}