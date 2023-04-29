package com.vinsonb.password.manager.kotlin.ui.features.createlogin

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
import com.vinsonb.password.manager.kotlin.ui.components.CustomTextField
import com.vinsonb.password.manager.kotlin.ui.theme.PassVaultTheme
import com.vinsonb.password.manager.kotlin.utilities.ScreenPreviews
import com.vinsonb.password.manager.kotlin.utilities.TextResIdProvider

@Composable
fun CreateLoginScreen(viewModel: CreateLoginViewModel) {
    val state by viewModel.stateFlow.collectAsState()

    CreateLoginContent(
        state = state,
        validatePasscode = viewModel::validatePasscode,
        validateRepeatPasscode = viewModel::validateRepeatPasscode,
        validateSecretQuestion = viewModel::validateSecretQuestion,
        validateSecretAnswer = viewModel::validateSecretAnswer,
        isValidPasscodeInput = viewModel::isValidPasscodeInput,
        createLogin = viewModel::createLogin,
    )
}

@Composable
private fun CreateLoginContent(
    state: CreateLoginState,
    validatePasscode: (String, String) -> Unit,
    validateRepeatPasscode: (String, String) -> Unit,
    validateSecretQuestion: (String) -> Unit,
    validateSecretAnswer: (String) -> Unit,
    isValidPasscodeInput: (String) -> Boolean,
    createLogin: (String, String, String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .imePadding()
            .navigationBarsPadding(),
    ) {
        var passcode by rememberSaveable { mutableStateOf("") }
        var repeatPasscode by rememberSaveable { mutableStateOf("") }

        CustomTextField.Password(
            text = passcode,
            onTextChange = {
                if (isValidPasscodeInput(it)) {
                    passcode = it
                    validatePasscode(it, repeatPasscode)
                }
            },
            label = stringResource(id = R.string.hint_passcode),
            isError = state.passcodeError != CreateLoginError.None,
            errorText = state.passcodeError.getErrorText(labelRes = R.string.hint_passcode),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        )

        CustomTextField.Password(
            text = repeatPasscode,
            onTextChange = {
                if (isValidPasscodeInput(it)) {
                    repeatPasscode = it
                    validateRepeatPasscode(passcode, it)
                }
            },
            label = stringResource(id = R.string.hint_repeat_passcode),
            isError = state.repeatPasscodeError != CreateLoginError.None,
            errorText = state.repeatPasscodeError.getErrorText(labelRes = R.string.hint_repeat_passcode),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        )

        var secretQuestion by rememberSaveable { mutableStateOf("") }
        CustomTextField.Normal(
            text = secretQuestion,
            onTextChange = {
                secretQuestion = it
                validateSecretQuestion(it)
            },
            label = stringResource(id = R.string.hint_secret_question),
            isError = state.secretQuestionError != CreateLoginError.None,
            errorText = state.secretQuestionError.getErrorText(labelRes = R.string.hint_secret_question),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        )

        var secretAnswer by rememberSaveable { mutableStateOf("") }
        CustomTextField.Normal(
            text = secretAnswer,
            onTextChange = {
                secretAnswer = it
                validateSecretAnswer(it)
            },
            label = stringResource(id = R.string.hint_secret_answer),
            isError = state.secretAnswerError != CreateLoginError.None,
            errorText = state.secretAnswerError.getErrorText(labelRes = R.string.hint_secret_answer),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = state.hasNoErrors(),
            onClick = { createLogin(passcode, secretQuestion, secretAnswer) },
        ) {
            Text(text = stringResource(id = R.string.button_create_login))
        }
    }
}

@ReadOnlyComposable
@Composable
fun CreateLoginError.getErrorText(@StringRes labelRes: Int) =
    if (this is TextResIdProvider) {
        stringResource(id = this.getTextResId(), stringResource(id = labelRes))
    } else {
        ""
    }

@ReadOnlyComposable
@Composable
fun CreateLoginState.hasNoErrors() = this.passcodeError == CreateLoginError.None
        && this.repeatPasscodeError == CreateLoginError.None
        && this.secretQuestionError == CreateLoginError.None
        && this.secretAnswerError == CreateLoginError.None

@ScreenPreviews
@Composable
fun PreviewCreateLogin() = PassVaultTheme {
    CreateLoginContent(
        state = CreateLoginState(),
        validatePasscode = { _, _ -> },
        validateRepeatPasscode = { _, _ -> },
        validateSecretQuestion = {},
        validateSecretAnswer = {},
        isValidPasscodeInput = { true },
        createLogin = { _, _, _ -> },
    )
}

@ScreenPreviews
@Composable
fun PreviewCreateLoginNoErrors() = PassVaultTheme {
    CreateLoginContent(
        state = CreateLoginState(
            passcodeError = CreateLoginError.None,
            repeatPasscodeError = CreateLoginError.None,
            secretQuestionError = CreateLoginError.None,
            secretAnswerError = CreateLoginError.None,
        ),
        validatePasscode = { _, _ -> },
        validateRepeatPasscode = { _, _ -> },
        validateSecretQuestion = {},
        validateSecretAnswer = {},
        isValidPasscodeInput = { true },
        createLogin = { _, _, _ -> },
    )
}
