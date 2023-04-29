package com.vinsonb.password.manager.kotlin.ui.features.forgotpasscode

import androidx.lifecycle.ViewModel
import com.vinsonb.password.manager.kotlin.di.CoroutineDispatchers
import com.vinsonb.password.manager.kotlin.extensions.stateIn
import com.vinsonb.password.manager.kotlin.ui.features.createlogin.CreateLoginViewModel
import com.vinsonb.password.manager.kotlin.utilities.Constants.Password.PASSCODE_MAX_LENGTH
import com.vinsonb.password.manager.kotlin.utilities.Constants.Password.PASSCODE_REGEX_PATTERN
import com.vinsonb.password.manager.kotlin.utilities.EventFlow
import com.vinsonb.password.manager.kotlin.utilities.SimpleToastEvent
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
) : ViewModel(), EventFlow<SimpleToastEvent> by simpleToastEventFlow(scope) {

    constructor(
        dispatchers: CoroutineDispatchers,
        savedSecretAnswer: String,
        saveNewPasscode: (String) -> Boolean,
    ) : this(
        scope = CoroutineScope(dispatchers.default),
        savedSecretAnswer = savedSecretAnswer,
        saveNewPasscode = saveNewPasscode,
    )

    private val _stateFlow = MutableStateFlow<ForgotPasscodeState>(ForgotPasscodeState.Hidden)
    val stateFlow = _stateFlow.stateIn(scope = scope, initialValue = ForgotPasscodeState.Hidden)

    fun validateSecretAnswer(secretAnswer: String) {
        if (_stateFlow.value is ForgotPasscodeState.Visible) {
            val errorState = when {
                secretAnswer.isBlank() -> ForgotPasscodeError.EmptyInputError
                secretAnswer != savedSecretAnswer -> ForgotPasscodeError.SecretAnswerMismatchError
                else -> ForgotPasscodeError.None
            }
            _stateFlow.update {
                (it as ForgotPasscodeState.Visible).copy(secretAnswerError = errorState)
            }
        }
    }

    fun validatePasscode(passcode: String, repeatPasscode: String) {
        if (_stateFlow.value is ForgotPasscodeState.Visible) {
            val errorState = when {
                passcode.isBlank() -> ForgotPasscodeError.EmptyInputError
                passcode.length != PASSCODE_MAX_LENGTH ->
                    ForgotPasscodeError.InvalidDigitsError
                else -> ForgotPasscodeError.None
            }
            _stateFlow.update {
                (it as ForgotPasscodeState.Visible).copy(passcodeError = errorState)
            }
            validateRepeatPasscode(passcode, repeatPasscode)
        }
    }

    fun validateRepeatPasscode(passcode: String, repeatPasscode: String) {
        if (_stateFlow.value is ForgotPasscodeState.Visible) {
            val errorState = when {
                repeatPasscode.isBlank() -> ForgotPasscodeError.EmptyInputError
                repeatPasscode.length != PASSCODE_MAX_LENGTH ->
                    ForgotPasscodeError.InvalidDigitsError
                passcode != repeatPasscode -> ForgotPasscodeError.PasscodeMismatchError
                else -> ForgotPasscodeError.None
            }
            _stateFlow.update {
                (it as ForgotPasscodeState.Visible).copy(repeatPasscodeError = errorState)
            }
        }
    }

    fun isValidPasscodeInput(passcode: String): Boolean {
        return passcode.matches(Regex(PASSCODE_REGEX_PATTERN))
    }

    fun resetPasscode(passcode: String) {
        if (saveNewPasscode(passcode)) {
            dismissDialog()
            sendEvent(SimpleToastEvent.ShowSucceeded)
        } else {
            sendEvent(SimpleToastEvent.ShowFailed)
        }
    }

    fun showDialog() {
        _stateFlow.update { ForgotPasscodeState.Visible() }
    }

    fun dismissDialog() {
        _stateFlow.update { ForgotPasscodeState.Hidden }
    }
}
