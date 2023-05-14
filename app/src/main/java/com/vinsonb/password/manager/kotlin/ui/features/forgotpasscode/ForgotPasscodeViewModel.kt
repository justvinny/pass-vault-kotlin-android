package com.vinsonb.password.manager.kotlin.ui.features.forgotpasscode

import androidx.lifecycle.ViewModel
import com.vinsonb.password.manager.kotlin.di.CoroutineDispatchers
import com.vinsonb.password.manager.kotlin.extensions.stateIn
import com.vinsonb.password.manager.kotlin.ui.features.createlogin.CreateLoginViewModel
import com.vinsonb.password.manager.kotlin.utilities.Constants.Password.PASSCODE_MAX_LENGTH
import com.vinsonb.password.manager.kotlin.utilities.SimpleToastEvent
import com.vinsonb.password.manager.kotlin.utilities.SimpleToastEventFlow
import com.vinsonb.password.manager.kotlin.utilities.simpleToastEventFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

/**
 * TODO: Can be refactored. Shares a lot of similarities with [CreateLoginViewModel].
 */
class ForgotPasscodeViewModel(
    private val scope: CoroutineScope,
    private val savedSecretAnswer: String,
    private val saveNewPasscode: (String) -> Boolean,
) : ViewModel(), SimpleToastEventFlow by simpleToastEventFlow(scope) {

    constructor(
        dispatchers: CoroutineDispatchers,
        savedSecretAnswer: String,
        saveNewPasscode: (String) -> Boolean,
    ) : this(
        scope = CoroutineScope(dispatchers.default),
        savedSecretAnswer = savedSecretAnswer,
        saveNewPasscode = saveNewPasscode,
    )

    private val _stateFlow = MutableStateFlow(ForgotPasscodeState())
    val stateFlow = _stateFlow.stateIn(scope = scope, initialValue = ForgotPasscodeState())

    fun validateSecretAnswer(secretAnswer: String) {
        val errorState = when {
            secretAnswer.isBlank() -> ForgotPasscodeError.EmptyInputError
            secretAnswer != savedSecretAnswer -> ForgotPasscodeError.SecretAnswerMismatchError
            else -> ForgotPasscodeError.None
        }
        _stateFlow.update {
            it.copy(secretAnswerError = errorState)
        }
    }

    fun validatePasscode(passcode: String, repeatPasscode: String) {
        val errorState = when {
            passcode.isBlank() -> ForgotPasscodeError.EmptyInputError
            passcode.length != PASSCODE_MAX_LENGTH ->
                ForgotPasscodeError.InvalidDigitsError
            else -> ForgotPasscodeError.None
        }
        _stateFlow.update {
            it.copy(passcodeError = errorState)
        }
        validateRepeatPasscode(passcode, repeatPasscode)
    }

    fun validateRepeatPasscode(passcode: String, repeatPasscode: String) {
        val errorState = when {
            repeatPasscode.isBlank() -> ForgotPasscodeError.EmptyInputError
            repeatPasscode.length != PASSCODE_MAX_LENGTH ->
                ForgotPasscodeError.InvalidDigitsError
            passcode != repeatPasscode -> ForgotPasscodeError.PasscodeMismatchError
            else -> ForgotPasscodeError.None
        }
        _stateFlow.update {
            it.copy(repeatPasscodeError = errorState)
        }
    }

    fun resetPasscode(passcode: String): Boolean {
        return saveNewPasscode(passcode).also { isSaved ->
            if (isSaved) {
                _stateFlow.update { ForgotPasscodeState() }
                sendEvent(SimpleToastEvent.ShowSucceeded)
            } else {
                sendEvent(SimpleToastEvent.ShowFailed)
            }
        }
    }
}
