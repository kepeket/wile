package com.wile.core.extensions

import android.content.Context
import android.widget.Toast

// Todo : consider replacing by Snackbar for nicer UI
fun Context.showToast(
    stringId: Int,
    duration: Int = Toast.LENGTH_SHORT
) = showToast(getString(stringId), duration)

fun Context.showToast(
    message: String,
    duration: Int = Toast.LENGTH_SHORT
) = Toast.makeText(this, message, duration)
        .show()
