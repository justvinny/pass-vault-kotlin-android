package com.vinsonb.password.manager.kotlin.ui.features.forgotpasscode

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.vinsonb.password.manager.kotlin.R
import com.vinsonb.password.manager.kotlin.ui.components.CustomTextField
import com.vinsonb.password.manager.kotlin.ui.theme.PassVaultTheme
import com.vinsonb.password.manager.kotlin.utilities.Constants
import com.vinsonb.password.manager.kotlin.utilities.ScreenPreviews
import com.vinsonb.password.manager.kotlin.utilities.TextResIdProvider

@Composable
fun ForgotPasscodeDialog(
    viewModel: ForgotPasscodeViewModel,
) {
    val state by viewModel.stateFlow.collectAsState()

    if (state is ForgotPasscodeState.Visible) {
        ForgotPasscodeContent(
            state = state as ForgotPasscodeState.Visible,
            dismissDialog = viewModel::dismissDialog,
            resetPasscode = viewModel::resetPasscode,
            validateSecretAnswer = viewModel::validateSecretAnswer,
            validatePasscode = viewModel::validatePasscode,
            validateRepeatPasscode = viewModel::validateRepeatPasscode,
        )
    }
}

@Composable
private fun ForgotPasscodeContent(
    state: ForgotPasscodeState.Visible,
    dismissDialog: () -> Unit,
    resetPasscode: (String) -> Unit,
    validateSecretAnswer: (String) -> Unit,
    validatePasscode: (String, String) -> Unit,
    validateRepeatPasscode: (String, String) -> Unit,
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
                label = stringResource(id = R.string.hint_secret_question),
                isError = state.secretAnswerErrorState != ForgotPasscodeErrors.None,
                errorText = state.secretAnswerErrorState.getErrorText(R.string.hint_secret_answer),
            )

            var newPasscode by rememberSaveable { mutableStateOf("") }
            var repeatNewPasscode by rememberSaveable { mutableStateOf("") }
            CustomTextField.Password(
                text = newPasscode,
                modifier = horizontalPadding,
                onTextChange = {
                    if (it.length <= Constants.Password.PASSCODE_MAX_LENGTH) {
                        newPasscode = it
                        validatePasscode(it, repeatNewPasscode)
                    }
                },
                label = stringResource(id = R.string.hint_new_passcode),
                isError = state.passcodeErrorState != ForgotPasscodeErrors.None,
                errorText = state.passcodeErrorState.getErrorText(R.string.hint_passcode),
            )
            CustomTextField.Password(
                text = repeatNewPasscode,
                modifier = horizontalPadding,
                onTextChange = {
                    if (it.length <= Constants.Password.PASSCODE_MAX_LENGTH) {
                        repeatNewPasscode = it
                        validateRepeatPasscode(newPasscode, it)
                    }
                },
                label = stringResource(id = R.string.hint_new_repeat_passcode),
                isError = state.repeatPasscodeErrorState != ForgotPasscodeErrors.None,
                errorText = state.repeatPasscodeErrorState.getErrorText(R.string.hint_new_passcode),
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
                    onClick = { resetPasscode(newPasscode) },
                ) {
                    Text(stringResource(id = R.string.button_reset))
                }
            }
        }
    }
}

@ReadOnlyComposable
@Composable
private fun ForgotPasscodeState.Visible.hasNoErrors() = (
        this.secretAnswerErrorState == ForgotPasscodeErrors.None
                && this.passcodeErrorState == ForgotPasscodeErrors.None
                && this.repeatPasscodeErrorState == ForgotPasscodeErrors.None
        )

@ReadOnlyComposable
@Composable
private fun ForgotPasscodeErrors.getErrorText(@StringRes labelRes: Int) =
    if (this is TextResIdProvider) {
        stringResource(id = this.getTextResId(), stringResource(id = labelRes))
    } else {
        ""
    }

@ScreenPreviews
@Composable
fun PreviewForgotPasswordDialogHasErrors() = PassVaultTheme {
    ForgotPasscodeContent(
        state = ForgotPasscodeState.Visible(),
        dismissDialog = {},
        resetPasscode = {},
        validateSecretAnswer = {},
        validatePasscode = { _, _ -> },
        validateRepeatPasscode = { _, _ -> },
    )
}

@ScreenPreviews
@Composable
fun PreviewForgotPasswordDialogNoErrors() = PassVaultTheme {
    ForgotPasscodeContent(
        state = ForgotPasscodeState.Visible(
            secretAnswerErrorState = ForgotPasscodeErrors.None,
            passcodeErrorState = ForgotPasscodeErrors.None,
            repeatPasscodeErrorState = ForgotPasscodeErrors.None,
        ),
        dismissDialog = {},
        resetPasscode = {},
        validateSecretAnswer = {},
        validatePasscode = { _, _ -> },
        validateRepeatPasscode = { _, _ -> },
    )
}
