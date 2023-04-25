package com.vinsonb.password.manager.kotlin.ui.features.createlogin

import androidx.lifecycle.ViewModel
import com.vinsonb.password.manager.kotlin.extensions.stateIn
import com.vinsonb.password.manager.kotlin.ui.features.forgotpasscode.ForgotPasscodeViewModel
import com.vinsonb.password.manager.kotlin.utilities.Constants
import com.vinsonb.password.manager.kotlin.utilities.Constants.Password.PASSCODE_MAX_LENGTH
import com.vinsonb.password.manager.kotlin.utilities.EventFlow
import com.vinsonb.password.manager.kotlin.utilities.SimpleToastEvent
import com.vinsonb.password.manager.kotlin.utilities.simpleToastEventFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

/**
 * TODO: Can be refactored. Shares a lot of similarities with [ForgotPasscodeViewModel].
 */
class CreateLoginViewModel(
    private val scope: CoroutineScope,
    private val _createLogin: (String, String, String) -> Unit,
) : ViewModel(), EventFlow<SimpleToastEvent> by simpleToastEventFlow(scope) {

    private val _stateFlow = MutableStateFlow(CreateLoginState())
    val stateFlow = _stateFlow.stateIn(scope, CreateLoginState())

    fun validatePasscode(passcode: String, repeatPasscode: String) {
        val error = when {
            passcode.isEmpty() -> CreateLoginError.EmptyInputError
            passcode.length != PASSCODE_MAX_LENGTH -> CreateLoginError.InvalidDigitsError
            else -> CreateLoginError.None
        }
        _stateFlow.update { it.copy(passcode = error) }
        validateRepeatPasscode(passcode, repeatPasscode)
    }

    fun validateRepeatPasscode(passcode: String, repeatPasscode: String) {
        val error = when {
            repeatPasscode.isEmpty() -> CreateLoginError.EmptyInputError
            repeatPasscode.length != PASSCODE_MAX_LENGTH -> CreateLoginError.InvalidDigitsError
            passcode != repeatPasscode -> CreateLoginError.PasscodeMismatchError
            else -> CreateLoginError.None
        }
        _stateFlow.update { it.copy(repeatPasscode = error) }
    }

    fun validateSecretQuestion(secretQuestion: String) {
        val error = if (secretQuestion.isBlank()) {
            CreateLoginError.EmptyInputError
        } else {
            CreateLoginError.None
        }
        _stateFlow.update { it.copy(secretQuestion = error) }
    }

    fun validateSecretAnswer(secretAnswer: String) {
        val error = if (secretAnswer.isBlank()) {
            CreateLoginError.EmptyInputError
        } else {
            CreateLoginError.None
        }
        _stateFlow.update { it.copy(secretAnswer = error) }
    }

    fun isValidPasscodeInput(passcode: String): Boolean {
        return passcode.matches(Regex(Constants.Password.PASSCODE_REGEX_PATTERN))
    }

    fun createLogin(passcode: String, secretQuestion: String, secretAnswer: String) {
        _createLogin(passcode, secretQuestion, secretAnswer)
    }
}