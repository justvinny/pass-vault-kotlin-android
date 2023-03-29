package com.vinsonb.password.manager.kotlin.ui.saveaccount

import androidx.annotation.StringRes
import com.vinsonb.password.manager.kotlin.R

data class SaveAccountState(
    val textFields: Map<TextFieldName, TextFieldState>,
) {
    enum class TextFieldName(@StringRes val hintRes: Int) {
        PLATFORM(R.string.hint_platform),
        USERNAME(R.string.hint_username),
        PASSWORD(R.string.hint_password),
        REPEAT_PASSWORD(R.string.hint_repeat_password),
    }

    data class TextFieldState(
        val text: String = "",
        val errorState: ErrorState = ErrorState.TEXT_EMPTY,
    ) {
        enum class ErrorState(@StringRes val errorRes: Int? = null) {
            NO_ERROR,
            TEXT_EMPTY(R.string.error_text_empty),
            PASSWORDS_MUST_MATCH(R.string.error_password_must_match)
        }
    }
}
