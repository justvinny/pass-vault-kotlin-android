package com.vinsonb.password.manager.kotlin.ui.features.forgotpasscode

import androidx.lifecycle.ViewModel
import com.vinsonb.password.manager.kotlin.di.CoroutineDispatchers
import com.vinsonb.password.manager.kotlin.utilities.Constants.Password.PASSCODE_MAX_LENGTH
import com.vinsonb.password.manager.kotlin.utilities.Constants.Password.PASSCODE_REGEX_PATTERN
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*

class ForgotPasscodeViewModel(
    private val scope: CoroutineScope,
    private val savedSecretAnswer: String,
    private val saveNewPasscode: (String) -> Boolean,
    private val showSucceededToast: () -> Unit,
    private val showFailedToast: () -> Unit,
) : ViewModel() {

    constructor(
        dispatchers: CoroutineDispatchers,
        savedSecretAnswer: String,
        saveNewPasscode: (String) -> Boolean,
        showSucceededToast: () -> Unit,
        showFailedToast: () -> Unit,
    ) : this(
        scope = CoroutineScope(dispatchers.default),
        savedSecretAnswer = savedSecretAnswer,
        saveNewPasscode = saveNewPasscode,
        showSucceededToast = showSucceededToast,
        showFailedToast = showFailedToast,
    )

    private val _stateFlow = MutableStateFlow<ForgotPasscodeState>(ForgotPasscodeState.Hidden)
    val stateFlow = _stateFlow
        .asStateFlow()
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ForgotPasscodeState.Hidden,
        )

    fun validateSecretAnswer(secretAnswer: String) {
        if (_stateFlow.value is ForgotPasscodeState.Visible) {
            val errorState = when {
                secretAnswer.isBlank() -> ForgotPasscodeErrors.EmptyInputError
                secretAnswer != savedSecretAnswer -> ForgotPasscodeErrors.SecretAnswerMismatchError
                else -> ForgotPasscodeErrors.None
            }
            _stateFlow.update {
                (it as ForgotPasscodeState.Visible).copy(secretAnswerErrorState = errorState)
            }
        }
    }

    fun validatePasscode(passcode: String, repeatPasscode: String) {
        if (_stateFlow.value is ForgotPasscodeState.Visible) {
            val errorState = when {
                passcode.isBlank() -> ForgotPasscodeErrors.EmptyInputError
                passcode.length != PASSCODE_MAX_LENGTH ->
                    ForgotPasscodeErrors.InvalidDigitsError
                else -> ForgotPasscodeErrors.None
            }
            _stateFlow.update {
                (it as ForgotPasscodeState.Visible).copy(passcodeErrorState = errorState)
            }
            validateRepeatPasscode(passcode, repeatPasscode)
        }
    }

    fun validateRepeatPasscode(passcode: String, repeatPasscode: String) {
        if (_stateFlow.value is ForgotPasscodeState.Visible) {
            val errorState = when {
                repeatPasscode.isBlank() -> ForgotPasscodeErrors.EmptyInputError
                repeatPasscode.length != PASSCODE_MAX_LENGTH ->
                    ForgotPasscodeErrors.InvalidDigitsError
                passcode != repeatPasscode -> ForgotPasscodeErrors.PasscodeMismatchError
                else -> ForgotPasscodeErrors.None
            }
            _stateFlow.update {
                (it as ForgotPasscodeState.Visible).copy(repeatPasscodeErrorState = errorState)
            }
        }
    }

    fun isValidPasscodeInput(passcode: String): Boolean {
        return passcode.matches(Regex(PASSCODE_REGEX_PATTERN))
    }

    fun resetPasscode(passcode: String) {
        if (saveNewPasscode(passcode)) {
            dismissDialog()
            showSucceededToast()
        } else {
            showFailedToast()
        }
    }

    fun showDialog() {
        _stateFlow.update { ForgotPasscodeState.Visible() }
    }

    fun dismissDialog() {
        _stateFlow.update { ForgotPasscodeState.Hidden }
    }
}
