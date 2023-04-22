package com.vinsonb.password.manager.kotlin.ui.features.forgotpasscode

import androidx.lifecycle.ViewModel
import com.vinsonb.password.manager.kotlin.utilities.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ForgotPasscodeViewModel(
    private val savedSecretAnswer: String,
    private val saveNewPasscode: (String) -> Boolean,
    private val showSucceededToast: () -> Unit,
    private val showFailedToast: () -> Unit,
) : ViewModel() {
    private val _stateFlow = MutableStateFlow<ForgotPasscodeState>(ForgotPasscodeState.Hidden)
    val stateFlow = _stateFlow.asStateFlow()

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
                passcode.length != Constants.Password.PASSCODE_MAX_LENGTH ->
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
                repeatPasscode.length != Constants.Password.PASSCODE_MAX_LENGTH ->
                    ForgotPasscodeErrors.InvalidDigitsError
                passcode != repeatPasscode -> ForgotPasscodeErrors.PasscodeMismatchError
                else -> ForgotPasscodeErrors.None
            }
            _stateFlow.update {
                (it as ForgotPasscodeState.Visible).copy(repeatPasscodeErrorState = errorState)
            }
        }
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
