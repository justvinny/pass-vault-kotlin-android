package com.vinsonb.password.manager.kotlin.ui.features.saveaccount

import androidx.lifecycle.ViewModel
import com.vinsonb.password.manager.kotlin.database.AccountRepository
import com.vinsonb.password.manager.kotlin.database.enitities.Account
import com.vinsonb.password.manager.kotlin.di.CoroutineDispatchers
import com.vinsonb.password.manager.kotlin.extensions.stateIn
import com.vinsonb.password.manager.kotlin.utilities.EventFlow
import com.vinsonb.password.manager.kotlin.utilities.SimpleToastEvent
import com.vinsonb.password.manager.kotlin.utilities.simpleToastEventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SaveAccountViewModel(
    private val scope: CoroutineScope,
    private val insertAccount: suspend (Account) -> Boolean,
) : ViewModel(), EventFlow<SimpleToastEvent> by simpleToastEventFlow(scope) {

    @Inject
    constructor(
        dispatchers: CoroutineDispatchers,
        accountRepository: AccountRepository,
    ) : this(
        scope = CoroutineScope(dispatchers.default),
        insertAccount = accountRepository::insertAccount,
    )

    private val _stateFlow = MutableStateFlow(SaveAccountState())
    val stateFlow = _stateFlow.stateIn(scope = scope, initialValue = SaveAccountState())

    fun validatePlatform(platform: String) {
        val error = if (platform.isEmpty()) SaveAccountError.EmptyInputError else SaveAccountError.None
        _stateFlow.update {
            it.copy(platformError = error)
        }
    }

    fun validateUsername(username: String) {
        val error = if (username.isEmpty()) SaveAccountError.EmptyInputError else SaveAccountError.None
        _stateFlow.update {
            it.copy(usernameError = error)
        }
    }

    fun validatePassword(password: String, repeatPassword: String) {
        val error = if (password.isEmpty()) SaveAccountError.EmptyInputError else SaveAccountError.None
        _stateFlow.update {
            it.copy(passwordError = error)
        }
        validateRepeatPassword(password, repeatPassword)
    }

    fun validateRepeatPassword(password: String, repeatPassword: String) {
        val error = when {
            repeatPassword.isEmpty() -> SaveAccountError.EmptyInputError
            repeatPassword != password -> SaveAccountError.PasswordMismatchError
            else -> SaveAccountError.None
        }
        _stateFlow.update {
            it.copy(repeatPasswordError = error)
        }
    }


    fun saveAccount(account: Account) {
        scope.launch {
            if (insertAccount(account)) {
                resetErrors()
                sendEvent(SimpleToastEvent.ShowSucceeded)
            } else {
                sendEvent(SimpleToastEvent.ShowFailed)
            }
        }
    }

    private fun resetErrors() {
        _stateFlow.update { SaveAccountState() }
    }
}
