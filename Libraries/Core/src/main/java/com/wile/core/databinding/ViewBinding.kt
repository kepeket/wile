package com.wile.core.databinding

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.wile.core.extensions.showToast

object ViewBinding {

    @SuppressLint("ClickableViewAccessibility")
    @JvmStatic
    @BindingAdapter(value = ["counterTarget", "counterDirection"], requireAll = true)
    fun bindCounterBtn(view: ImageButton, counterTarget: TextView, counterDirection: String?) {
        val lambda = fun() {
            val stringNumber = counterTarget.text.toString()
            var curValue = if (stringNumber.isNotBlank()) stringNumber.toInt() else 0
            if (counterDirection == "sub"){
                curValue = if (curValue > 0) { curValue-1 } else { 0 }
            } else {
                curValue++
            }
            counterTarget.setText(curValue.toString())
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
}
