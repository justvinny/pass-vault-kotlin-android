package com.vinsonb.password.manager.kotlin.ui.features.login

import androidx.lifecycle.ViewModel
import com.vinsonb.password.manager.kotlin.di.CoroutineDispatchers
import com.vinsonb.password.manager.kotlin.ui.features.login.LoginState.Companion.MAX_PASSCODE_DIGITS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
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
    val stateFlow = _stateFlow.asStateFlow()
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = LoginState(),
        )

    fun onEnterPasscodeDigit(digit: Int) {
        if (passcodeDigitsEntered.size < MAX_PASSCODE_DIGITS) {
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
