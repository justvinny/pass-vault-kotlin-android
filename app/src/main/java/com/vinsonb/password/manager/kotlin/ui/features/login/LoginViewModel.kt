package com.vinsonb.password.manager.kotlin.ui.features.login

import androidx.lifecycle.ViewModel
import com.vinsonb.password.manager.kotlin.di.CoroutineDispatchers
import com.vinsonb.password.manager.kotlin.extensions.stateIn
import com.vinsonb.password.manager.kotlin.utilities.Constants.Password.PASSCODE_MAX_LENGTH
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LoginViewModel(
    scope: CoroutineScope,
) : ViewModel() {

    @Inject
    constructor(
        dispatcher: CoroutineDispatchers,
    ) : this(
        scope = CoroutineScope(dispatcher.default)
    )

    private val passcodeDigitsEntered = ArrayDeque<Int>()
    private val _stateFlow = MutableStateFlow(LoginState())
    val stateFlow = _stateFlow.stateIn(scope = scope, initialValue = LoginState())

    fun onEnterPasscodeDigit(digit: Int) {
        if (passcodeDigitsEntered.size < PASSCODE_MAX_LENGTH) {
            passcodeDigitsEntered.addLast(digit)
            _stateFlow.update {
                it.copy(
                    passcode = passcodeDigitsEntered.joinToString(separator = ""),
                    passcodeLength = passcodeDigitsEntered.size,
                )
            }
        }
    }

    fun onClearLastDigit() {
        passcodeDigitsEntered.removeLastOrNull()?.also {
            _stateFlow.update { state ->
                state.copy(
                    passcode = passcodeDigitsEntered.joinToString(separator = ""),
                    passcodeLength = passcodeDigitsEntered.size,
                )
            }
        }
    }

    fun onClearAllDigits() {
        passcodeDigitsEntered.clear()
        _stateFlow.update {
            it.copy(
                passcode = passcodeDigitsEntered.joinToString(separator = ""),
                passcodeLength = passcodeDigitsEntered.size,
            )
        }
    }
}
