package com.vinsonb.password.manager.kotlin.ui.features.createlogin

import com.vinsonb.password.manager.kotlin.R
import com.vinsonb.password.manager.kotlin.ui.features.createlogin.CreateLoginError.EmptyInputError
import com.vinsonb.password.manager.kotlin.utilities.TextResIdProvider
import com.vinsonb.password.manager.kotlin.utilities.textResIdEmptyInputProvider
import com.vinsonb.password.manager.kotlin.utilities.textResIdProvider

data class CreateLoginState(
    val passcode: CreateLoginError = EmptyInputError,
    val repeatPasscode: CreateLoginError = EmptyInputError,
    val secretQuestion: CreateLoginError = EmptyInputError,
    val secretAnswer: CreateLoginError = EmptyInputError,
)

sealed interface CreateLoginError {
    object None : CreateLoginError
    object EmptyInputError : CreateLoginError, TextResIdProvider by textResIdEmptyInputProvider()
    object InvalidDigitsError : CreateLoginError,
        TextResIdProvider by textResIdProvider(R.string.error_passcode_length)
    object PasscodeMismatchError : CreateLoginError,
        TextResIdProvider by textResIdProvider(R.string.error_passcode_must_match)
}
