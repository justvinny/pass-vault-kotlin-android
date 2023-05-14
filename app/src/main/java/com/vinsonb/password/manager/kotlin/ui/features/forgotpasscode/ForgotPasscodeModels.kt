package com.vinsonb.password.manager.kotlin.ui.features.forgotpasscode

import com.vinsonb.password.manager.kotlin.R
import com.vinsonb.password.manager.kotlin.utilities.TextResIdProvider
import com.vinsonb.password.manager.kotlin.utilities.textResIdEmptyInputProvider
import com.vinsonb.password.manager.kotlin.utilities.textResIdProvider

data class ForgotPasscodeState(
    val secretAnswerError: ForgotPasscodeError = ForgotPasscodeError.EmptyInputError,
    val passcodeError: ForgotPasscodeError = ForgotPasscodeError.EmptyInputError,
    val repeatPasscodeError: ForgotPasscodeError = ForgotPasscodeError.EmptyInputError,
)

sealed interface ForgotPasscodeError {
    object None : ForgotPasscodeError
    object SecretAnswerMismatchError : ForgotPasscodeError,
        TextResIdProvider by textResIdProvider(R.string.error_wrong_secret_answer)

    object PasscodeMismatchError : ForgotPasscodeError,
        TextResIdProvider by textResIdProvider(R.string.error_passcode_must_match)

    object InvalidDigitsError : ForgotPasscodeError,
        TextResIdProvider by textResIdProvider(R.string.error_passcode_length)

    object EmptyInputError : ForgotPasscodeError,
        TextResIdProvider by textResIdEmptyInputProvider()
}
