package com.wile.app.binding

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.View
import android.widget.*
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.cardview.widget.CardView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import com.wile.app.R
import com.wile.core.extensions.showToast
import com.wile.app.ui.handler.WorkoutInterface
import timber.log.Timber
import java.lang.Integer.parseInt

object ViewBinding {

    @JvmStatic
    @BindingAdapter("android:text")
    fun bindTextValue(view: TextView, text: String?){
        // FixMe : i think the nullability check is not useful ; how did you do if you want to clear a field ?
        text?.let {
            view.text = text
        }
    }

    @JvmStatic
    @InverseBindingAdapter(attribute = "android:text", event = "android:textAttrChanged")
    fun binIntToTextValue(view: TextView): Int {
        if (!view.text.isNullOrBlank()) {
            return parseInt(view.text.toString())
        }
        return 0
    }


    @JvmStatic
    @BindingAdapter("android:text")
    fun bindIntValue(view: TextView, value: Int) {
        view.text = value.toString()
    }

    @JvmStatic
    @BindingAdapter("duration")
    fun bindDurationValue(view: TextView, duration: Int){
        val sec = duration % 60
        val min = duration / 60
        view.text = view.context.getString(R.string.overall_duration, min, sec)
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
                    curValue = if (curValue > 0) { curValue-1 } else { 0 }
                } else {
                    curValue++
                }
                textview.text = curValue.toString()
            }
        }

        view.setOnClickListener { lambda() }

        view.setOnTouchListener(
            RepeatListener(initialInterval = 400, initialRepeatDelay = 100) { lambda() }
        )
    }

    @JvmStatic
    @BindingAdapter("toast")
    fun bindToast(view: View, text: Int) {
        view.context.showToast(text)
    }

    @JvmStatic
    @BindingAdapter("toast")
    fun bindToast(view: View, text: String?) {
        text?.let {
            if (it.isNotEmpty()) {
                view.context.showToast(it)
            }
        }
    }

    @JvmStatic
    @BindingAdapter("tintColor")
    fun bindTintColor(view: ImageView, tint: Int) {
        view.imageTintList = ColorStateList.valueOf(tint)
    }

    @JvmStatic
    @BindingAdapter("gone")
    fun bindGone(view: View, shouldBeGone: Boolean) {
        Timber.d("view id %s", view.id)
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
        view.findViewById<View>(R.id.stop).setOnClickListener { controller.askStopWorkout() }
        view.findViewById<View>(R.id.pause).setOnClickListener { controller.askStartPauseWorkout() }
        view.findViewById<View>(R.id.next).setOnClickListener { controller.askSkipTraining() }
        view.findViewById<CardView>(R.id.muscle_up_card).setOnClickListener { controller.askSkipTraining() }
    }
}
