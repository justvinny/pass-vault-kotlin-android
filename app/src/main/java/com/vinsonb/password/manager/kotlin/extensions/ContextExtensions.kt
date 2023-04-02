package com.vinsonb.password.manager.kotlin.extensions

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.showToast(@StringRes strResId: Int, @StringRes vararg additionalStrResIds: Int) {
    val additionalString = if (additionalStrResIds.isNotEmpty()) {
        additionalStrResIds.map { this.getString(it) }
    } else {
        emptyList()
    }

    if (additionalString.isNotEmpty()) {
        showToast(getString(strResId, *additionalString.toTypedArray()))
    } else {
        showToast(getString(strResId))
    }
}
