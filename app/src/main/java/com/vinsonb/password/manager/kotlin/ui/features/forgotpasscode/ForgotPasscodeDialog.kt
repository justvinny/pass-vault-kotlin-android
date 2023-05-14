package com.vinsonb.password.manager.kotlin.ui.features.forgotpasscode

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.vinsonb.password.manager.kotlin.R
import com.vinsonb.password.manager.kotlin.ui.components.CustomTextField
import com.vinsonb.password.manager.kotlin.ui.theme.PassVaultTheme
import com.vinsonb.password.manager.kotlin.utilities.ScreenPreviews
import com.vinsonb.password.manager.kotlin.utilities.TextResIdProvider
import com.vinsonb.password.manager.kotlin.utilities.isValidPasscodeInput

@Composable
fun ForgotPasscodeDialog(
    state: ForgotPasscodeState,
    resetPasscode: (String) -> Boolean,
    validateSecretAnswer: (String) -> Unit,
    validatePasscode: (String, String) -> Unit,
    validateRepeatPasscode: (String, String) -> Unit,
    secretQuestion: String,
    dismissDialog: () -> Unit,
) {
    Dialog(onDismissRequest = dismissDialog) {
        Card(modifier = Modifier.fillMaxWidth()) {
            val horizontalPadding = Modifier.padding(horizontal = 16.dp)

            Text(
                modifier = horizontalPadding.then(Modifier.padding(vertical = 16.dp)),
                text = stringResource(R.string.text_title_forgot),
                style = MaterialTheme.typography.headlineSmall
                    .copy(color = MaterialTheme.colorScheme.primary),
            )

            var secretAnswer by rememberSaveable { mutableStateOf("") }
            CustomTextField.Password(
                text = secretAnswer,
                modifier = horizontalPadding,
                onTextChange = {
                    secretAnswer = it
                    validateSecretAnswer(it)
                },
                label = secretQuestion,
                isError = state.secretAnswerError != ForgotPasscodeError.None,
                errorText = state.secretAnswerError.getErrorText(R.string.hint_secret_answer),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            )

            var newPasscode by rememberSaveable { mutableStateOf("") }
            var repeatNewPasscode by rememberSaveable { mutableStateOf("") }
            CustomTextField.Password(
                text = newPasscode,
                modifier = horizontalPadding,
                onTextChange = {
                    if (isValidPasscodeInput(it)) {
                        newPasscode = it
                        validatePasscode(it, repeatNewPasscode)
                    }
                },
                label = stringResource(id = R.string.hint_new_passcode),
                isError = state.passcodeError != ForgotPasscodeError.None,
                errorText = state.passcodeError.getErrorText(R.string.hint_passcode),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next,
                ),
            )
            CustomTextField.Password(
                text = repeatNewPasscode,
                modifier = horizontalPadding,
                onTextChange = {
                    if (isValidPasscodeInput(it)) {
                        repeatNewPasscode = it
                        validateRepeatPasscode(newPasscode, it)
                    }
                },
                label = stringResource(id = R.string.hint_new_repeat_passcode),
                isError = state.repeatPasscodeError != ForgotPasscodeError.None,
                errorText = state.repeatPasscodeError.getErrorText(R.string.hint_new_passcode),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
            )

            Row(
                modifier = horizontalPadding.then(
                    other = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                ),
                horizontalArrangement = Arrangement.End,
            ) {
                Button(
                    enabled = state.hasNoErrors(),
                    onClick = {
                        if (resetPasscode(newPasscode)) {
                            newPasscode = ""
                            repeatNewPasscode = ""
                            dismissDialog()
                        }
                    },
                ) {
                    Text(stringResource(id = R.string.button_reset))
                }
            }
        }
    }
}

@ReadOnlyComposable
@Composable
private fun ForgotPasscodeState.hasNoErrors() = (
        this.secretAnswerError == ForgotPasscodeError.None
                && this.passcodeError == ForgotPasscodeError.None
                && this.repeatPasscodeError == ForgotPasscodeError.None
        )

@ReadOnlyComposable
@Composable
private fun ForgotPasscodeError.getErrorText(@StringRes labelRes: Int) =
    if (this is TextResIdProvider) {
        stringResource(id = this.getTextResId(), stringResource(id = labelRes))
    } else {
        ""
    }

@ScreenPreviews
@Composable
fun PreviewForgotPasswordDialogHasErrors() = PassVaultTheme {
    ForgotPasscodeDialog(
        state = ForgotPasscodeState(),
        dismissDialog = {},
        resetPasscode = { true },
        validateSecretAnswer = {},
        validatePasscode = { _, _ -> },
        validateRepeatPasscode = { _, _ -> },
        secretQuestion = "Secret Question",
    )
}

@ScreenPreviews
@Composable
fun PreviewForgotPasswordDialogNoErrors() = PassVaultTheme {
    ForgotPasscodeDialog(
        state = ForgotPasscodeState(
            secretAnswerError = ForgotPasscodeError.None,
            passcodeError = ForgotPasscodeError.None,
            repeatPasscodeError = ForgotPasscodeError.None,
        ),
        dismissDialog = {},
        resetPasscode = { true },
        validateSecretAnswer = {},
        validatePasscode = { _, _ -> },
        validateRepeatPasscode = { _, _ -> },
        secretQuestion = "Secret Question",
    )
}
