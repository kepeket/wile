package com.wile.main.binding

import android.os.Handler
import android.view.MotionEvent
import android.view.View

class RepeatListener(
        initialInterval: Int,
        initialRepeatDelay: Int,
        clickListener: View.OnClickListener
) : View.OnTouchListener {

    private val handler = Handler()

    private var initialInterval: Int
    private var initialRepeatDelay: Int

    private var clickListener: View.OnClickListener
    private var touchedView: View? = null

    init {
        require(!(initialInterval < 0 || initialRepeatDelay < 0)) { "negative intervals not allowed" }

        this.initialInterval = initialRepeatDelay
        this.initialRepeatDelay = initialInterval

        this.clickListener = clickListener
    }

    private val handlerRunnable: Runnable = run {
        Runnable {
            if (touchedView!!.isEnabled) {

                handler.postDelayed(handlerRunnable, initialRepeatDelay.toLong())
                clickListener.onClick(touchedView)
            } else {

                // if the view was disabled by the clickListener, remove the callback
                handler.removeCallbacks(handlerRunnable)
                touchedView!!.isPressed = false
                touchedView = null
            }
        }
    }

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {

        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                handler.removeCallbacks(handlerRunnable)
                handler.postDelayed(handlerRunnable, initialRepeatDelay.toLong())
                touchedView = view
                touchedView!!.isPressed = true
                clickListener.onClick(view)
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                handler.removeCallbacks(handlerRunnable)
                touchedView!!.isPressed = false
                touchedView = null
                return true
            }
        }

        return false
    }
}