package com.vinsonb.password.manager.kotlin.ui.features.saveaccount

import androidx.lifecycle.ViewModel
import com.vinsonb.password.manager.kotlin.database.AccountRepository
import com.vinsonb.password.manager.kotlin.database.enitities.Account
import com.vinsonb.password.manager.kotlin.ui.features.saveaccount.SaveAccountState.TextFieldName
import com.vinsonb.password.manager.kotlin.ui.features.saveaccount.SaveAccountState.TextFieldName.*
import com.vinsonb.password.manager.kotlin.ui.features.saveaccount.SaveAccountState.TextFieldState
import com.vinsonb.password.manager.kotlin.ui.features.saveaccount.SaveAccountState.TextFieldState.ErrorState.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SaveAccountViewModel(
    private val insertAccount: suspend (Account) -> Boolean,
) : ViewModel() {

    @Inject
    constructor(
        accountRepository: AccountRepository,
    ) : this(
        insertAccount = accountRepository::insertAccount,
    )

    private val platformFlow = MutableStateFlow(TextFieldState())
    private val usernameFlow = MutableStateFlow(TextFieldState())
    private val passwordFlow = MutableStateFlow(TextFieldState())
    private val repeatPasswordFlow = MutableStateFlow(TextFieldState())

    private val flowMap = mapOf(
        PLATFORM to platformFlow,
        USERNAME to usernameFlow,
        PASSWORD to passwordFlow,
        REPEAT_PASSWORD to repeatPasswordFlow,
    )
    val stateFlow = combine(
        platformFlow,
        usernameFlow,
        passwordFlow,
        repeatPasswordFlow
    ) { platform, username, password, repeatPassword ->
        SaveAccountState(
            mapOf(
                PLATFORM to platform,
                USERNAME to username,
                PASSWORD to password,
                REPEAT_PASSWORD to repeatPassword,
            )
        )
    }

    fun validate(textFieldName: TextFieldName) {
        flowMap[textFieldName]?.also {
            if (textFieldName == PASSWORD) {
                validate(REPEAT_PASSWORD)
            }

            val doPasswordsMatch = repeatPasswordFlow.value.text == passwordFlow.value.text
            val errorState = when {
                it.value.text.isBlank() -> TEXT_EMPTY
                textFieldName == REPEAT_PASSWORD && !doPasswordsMatch -> PASSWORDS_MUST_MATCH
                else -> NO_ERROR
            }

            flowMap[textFieldName]?.update { oldState ->
                oldState.copy(errorState = errorState)
            }
        }
    }

    fun validateAll() {
        for (map in flowMap) {
            validate(map.key)
        }
    }

    fun onTextChange(textFieldName: TextFieldName, newText: String) {
        flowMap[textFieldName]?.update { oldState ->
            oldState.copy(text = newText)
        }
    }

    suspend fun saveAccount(): Boolean {
        val hasNoErrors = flowMap.values.all { it.value.errorState == NO_ERROR }
        if (hasNoErrors) {
            val account = Account(
                platform = platformFlow.value.text,
                username = usernameFlow.value.text,
                password = passwordFlow.value.text,
            )

            return if (insertAccount(account)) {
                clearStates()
                true
            } else {
                false
            }
        }
        return false
    }

    private fun clearStates() {
        flowMap.values.forEach {
            it.value = TextFieldState()
        }
    }
}
