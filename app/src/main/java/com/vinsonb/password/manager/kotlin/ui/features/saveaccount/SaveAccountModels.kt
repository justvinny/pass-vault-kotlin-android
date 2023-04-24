package com.vinsonb.password.manager.kotlin.ui.features.saveaccount

import com.vinsonb.password.manager.kotlin.R
import com.vinsonb.password.manager.kotlin.ui.features.saveaccount.SaveAccountError.EmptyInputError
import com.vinsonb.password.manager.kotlin.utilities.TextResIdProvider
import com.vinsonb.password.manager.kotlin.utilities.textResIdEmptyInputProvider
import com.vinsonb.password.manager.kotlin.utilities.textResIdProvider

data class SaveAccountState(
    val platformError: SaveAccountError = EmptyInputError,
    val usernameError: SaveAccountError = EmptyInputError,
    val passwordError: SaveAccountError = EmptyInputError,
    val repeatPasswordError: SaveAccountError = EmptyInputError,
)

sealed interface SaveAccountError {
    object None : SaveAccountError
    object EmptyInputError : SaveAccountError, TextResIdProvider by textResIdEmptyInputProvider()
    object PasswordMismatchError : SaveAccountError,
        TextResIdProvider by textResIdProvider(R.string.error_password_must_match)
}
