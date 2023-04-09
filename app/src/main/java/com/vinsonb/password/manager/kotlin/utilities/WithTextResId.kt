package com.vinsonb.password.manager.kotlin.utilities

import androidx.annotation.StringRes

interface TextResIdProvider {
    @StringRes
    fun getTextResId(): Int
}

fun textResIdProvider(@StringRes textResId: Int) = object : TextResIdProvider {
    override fun getTextResId(): Int = textResId
}
