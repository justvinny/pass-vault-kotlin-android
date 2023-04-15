package com.vinsonb.password.manager.kotlin.ui.features.login

import com.vinsonb.password.manager.kotlin.R


data class LoginState(
    val passcode: String = "",
    val passcodeLength: Int = 0,
) {
    companion object {
        const val MAX_PASSCODE_DIGITS = 5
        const val BUTTON_GROUP_ROW_COUNT = 4
        const val BUTTON_GROUP_ROW_N_ITEMS = 3
        const val BUTTON_GROUP_ROW_N_ITEMS_LAST_INDEX = BUTTON_GROUP_ROW_N_ITEMS - 1
        val BUTTON_LABEL_LIST = listOf(
            R.string.button_1,
            R.string.button_2,
            R.string.button_3,
            R.string.button_4,
            R.string.button_5,
            R.string.button_6,
            R.string.button_7,
            R.string.button_8,
            R.string.button_9,
            R.string.button_forgot_password,
            R.string.button_0,
            R.string.button_clear,
        )
    }
}
