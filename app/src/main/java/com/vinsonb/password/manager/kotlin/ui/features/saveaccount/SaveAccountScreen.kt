package com.vinsonb.password.manager.kotlin.ui.features.saveaccount

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.vinsonb.password.manager.kotlin.R
import com.vinsonb.password.manager.kotlin.database.enitities.Account
import com.vinsonb.password.manager.kotlin.ui.components.CustomTextField
import com.vinsonb.password.manager.kotlin.ui.theme.PassVaultTheme
import com.vinsonb.password.manager.kotlin.utilities.ScreenPreviews
import com.vinsonb.password.manager.kotlin.utilities.SimpleToastEvent
import com.vinsonb.password.manager.kotlin.utilities.TextResIdProvider
import com.vinsonb.password.manager.kotlin.utilities.clearMutableStateStrings

@Composable
fun SaveAccountScreen(viewModel: SaveAccountViewModel) {
    val state by viewModel.stateFlow.collectAsState()
    val shouldClearAllInput = viewModel.eventFlow.collectAsState(
        initial = SimpleToastEvent.None
    ).value == SimpleToastEvent.ShowSucceeded

    SaveAccountContent(
        state = state,
        shouldClearAllInput = shouldClearAllInput,
        validatePlatform = viewModel::validatePlatform,
        validateUsername = viewModel::validateUsername,
        validatePassword = viewModel::validatePassword,
        validateRepeatPassword = viewModel::validateRepeatPassword,
        saveAccount = viewModel::saveAccount,
        resetEvent = viewModel::resetEvent,
    )
}

@Composable
private fun SaveAccountContent(
    state: SaveAccountState,
    shouldClearAllInput: Boolean,
    validatePlatform: (String) -> Unit,
    validateUsername: (String) -> Unit,
    validatePassword: (String, String) -> Unit,
    validateRepeatPassword: (String, String) -> Unit,
    saveAccount: (Account) -> Unit,
    resetEvent: () -> Unit,
) {
    val platform = rememberSaveable { mutableStateOf("") }
    val username = rememberSaveable { mutableStateOf("") }
    val password = rememberSaveable { mutableStateOf("") }
    val repeatPassword = rememberSaveable { mutableStateOf("") }

    if (shouldClearAllInput) {
        clearMutableStateStrings(platform, username, password, repeatPassword)
        resetEvent()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .imePadding()
            .navigationBarsPadding(),
    ) {
        CustomTextField.Normal(
            text = platform.value,
            onTextChange = {
                platform.value = it
                validatePlatform(it)
            },
            label = stringResource(id = R.string.hint_platform),
            isError = state.platformError != SaveAccountError.None,
            errorText = state.platformError.getErrorText(R.string.hint_platform),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        )

        CustomTextField.Normal(
            text = username.value,
            onTextChange = {
                username.value = it
                validateUsername(it)
            },
            label = stringResource(id = R.string.hint_username),
            isError = state.usernameError != SaveAccountError.None,
            errorText = state.usernameError.getErrorText(R.string.hint_username),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        )

        CustomTextField.Password(
            text = password.value,
            onTextChange = {
                password.value = it
                validatePassword(it, repeatPassword.value)
            },
            label = stringResource(id = R.string.hint_password),
            isError = state.passwordError != SaveAccountError.None,
            errorText = state.passwordError.getErrorText(R.string.hint_password),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        )

        CustomTextField.Password(
            text = repeatPassword.value,
            onTextChange = {
                repeatPassword.value = it
                validateRepeatPassword(password.value, it)
            },
            label = stringResource(id = R.string.hint_repeat_password),
            isError = state.repeatPasswordError != SaveAccountError.None,
            errorText = state.repeatPasswordError.getErrorText(R.string.hint_repeat_password),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                saveAccount(
                    Account(
                        platform = platform.value,
                        username = username.value,
                        password = password.value,
                    )
                )
            },
            enabled = state.hasNoErrors(),
        ) {
            Text(stringResource(id = R.string.button_save_account))
        }
    }
}

@ReadOnlyComposable
@Composable
private fun SaveAccountError.getErrorText(@StringRes additionalId: Int) =
    if (this == SaveAccountError.None) {
        ""
    } else {
        stringResource(
            id = (this as TextResIdProvider).getTextResId(),
            stringResource(id = additionalId),
        )
    }

@ReadOnlyComposable
@Composable
private fun SaveAccountState.hasNoErrors() = (
        this.platformError == SaveAccountError.None
                && this.usernameError == SaveAccountError.None
                && this.passwordError == SaveAccountError.None
                && this.repeatPasswordError == SaveAccountError.None
        )

@ScreenPreviews
@Composable
private fun PreviewSaveAccountScreen() = PassVaultTheme {
    SaveAccountContent(
        state = SaveAccountState(),
        shouldClearAllInput = false,
        validatePlatform = {},
        validateUsername = {},
        validatePassword = { _, _ -> },
        validateRepeatPassword = { _, _ -> },
        saveAccount = {},
        resetEvent = {},
    )
}

@ScreenPreviews
@Composable
private fun PreviewSaveAccountScreenNoErrors() = PassVaultTheme {
    SaveAccountContent(
        state = SaveAccountState(
            platformError = SaveAccountError.None,
            usernameError = SaveAccountError.None,
            passwordError = SaveAccountError.None,
            repeatPasswordError = SaveAccountError.None,
        ),
        shouldClearAllInput = false,
        validatePlatform = {},
        validateUsername = {},
        validatePassword = { _, _ -> },
        validateRepeatPassword = { _, _ -> },
        saveAccount = {},
        resetEvent = {},
    )
}
