package com.vinsonb.password.manager.kotlin.ui.features.forgotpasscode

import com.vinsonb.password.manager.kotlin.R
import com.vinsonb.password.manager.kotlin.ui.features.forgotpasscode.ForgotPasscodeErrors.EmptyInputError
import com.vinsonb.password.manager.kotlin.utilities.TextResIdProvider
import com.vinsonb.password.manager.kotlin.utilities.textResIdProvider

sealed interface ForgotPasscodeState {
    object Hidden : ForgotPasscodeState

    data class Visible(
        val secretAnswerErrorState: ForgotPasscodeErrors = EmptyInputError,
        val passcodeErrorState: ForgotPasscodeErrors = EmptyInputError,
        val repeatPasscodeErrorState: ForgotPasscodeErrors = EmptyInputError,
    ) : ForgotPasscodeState
}

sealed interface ForgotPasscodeErrors {
    object None : ForgotPasscodeErrors
    object SecretAnswerMismatchError : ForgotPasscodeErrors,
        TextResIdProvider by textResIdProvider(R.string.error_wrong_secret_answer)

    object PasscodeMismatchError : ForgotPasscodeErrors,
        TextResIdProvider by textResIdProvider(R.string.error_passcode_must_match)

    object InvalidDigitsError : ForgotPasscodeErrors,
        TextResIdProvider by textResIdProvider(R.string.error_passcode_length)

    object EmptyInputError : ForgotPasscodeErrors,
        TextResIdProvider by textResIdProvider(R.string.error_text_empty)
}
