package com.vinsonb.password.manager.kotlin.utilities

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

interface TextResIdProvider {
    @StringRes
    fun getTextResId(): Int
    fun showToast(context: Context)
}

fun textResIdProvider(@StringRes textResId: Int) = object : TextResIdProvider {
    override fun getTextResId(): Int = textResId
    override fun showToast(context: Context) {
        Toast.makeText(
            context,
            getTextResId(),
            Toast.LENGTH_SHORT,
        ).show()
    }
}
