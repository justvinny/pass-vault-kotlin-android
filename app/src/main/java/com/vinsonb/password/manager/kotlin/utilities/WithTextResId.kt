package com.vinsonb.password.manager.kotlin.utilities

import androidx.annotation.StringRes
import com.vinsonb.password.manager.kotlin.R

interface TextResIdProvider {
    @StringRes
    fun getTextResId(): Int
}

fun textResIdProvider(@StringRes textResId: Int) = object : TextResIdProvider {
    override fun getTextResId(): Int = textResId
}

fun textResIdEmptyInputProvider() = object : TextResIdProvider {
    override fun getTextResId(): Int = R.string.error_text_empty
}
