package com.vinsonb.password.manager.kotlin.ui.saveaccount

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.vinsonb.password.manager.kotlin.R
import com.vinsonb.password.manager.kotlin.extensions.showToast
import com.vinsonb.password.manager.kotlin.ui.components.CustomTextField
import com.vinsonb.password.manager.kotlin.ui.saveaccount.SaveAccountState.TextFieldName
import com.vinsonb.password.manager.kotlin.ui.saveaccount.SaveAccountState.TextFieldName.*
import com.vinsonb.password.manager.kotlin.ui.saveaccount.SaveAccountState.TextFieldState
import com.vinsonb.password.manager.kotlin.ui.saveaccount.SaveAccountState.TextFieldState.ErrorState
import com.vinsonb.password.manager.kotlin.ui.saveaccount.SaveAccountState.TextFieldState.ErrorState.*
import com.vinsonb.password.manager.kotlin.ui.theme.PassVaultTheme
import com.vinsonb.password.manager.kotlin.utilities.ScreenPreviews
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SaveAccountScreen(viewModel: SaveAccountViewModel) {
    val state by viewModel.stateFlow.collectAsState(SaveAccountState(emptyMap()))

    /**
     * This ensures it does not run again after recomposition as we only want validateAll
     * to be called once. Subsequent validations are handled individually in onTextChange
     * on our Text Fields.
     */
    LaunchedEffect(Unit) {
        viewModel.validateAll()
    }

    SaveAccountContent(
        state = state,
        onTextChange = viewModel::onTextChange,
        saveAccount = viewModel::saveAccount,
        validate = viewModel::validate,
    )
}

@Composable
private fun SaveAccountContent(
    state: SaveAccountState,
    onTextChange: (TextFieldName, String) -> Unit,
    saveAccount: suspend () -> Boolean,
    validate: (TextFieldName) -> Unit,
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .imePadding()
            .navigationBarsPadding(),
    ) {
        for (textField in state.textFields) {
            when (textField.key) {
                PASSWORD, REPEAT_PASSWORD -> CustomTextField.Password(
                    text = textField.value.text,
                    onTextChange = { newText ->
                        onTextChange(textField.key, newText)
                        validate(textField.key)
                    },
                    label = stringResource(textField.key.hintRes),
                    isError = textField.value.errorState != NO_ERROR,
                    errorText = textField.value.errorState.getErrorText(textField.key.hintRes),
                    keyboardOptions = textField.key.getKeyboardOptions(),
                )
                else -> CustomTextField.Normal(
                    text = textField.value.text,
                    onTextChange = { newText ->
                        onTextChange(textField.key, newText)
                        validate(textField.key)
                    },
                    label = stringResource(textField.key.hintRes),
                    isError = textField.value.errorState != NO_ERROR,
                    errorText = textField.value.errorState.getErrorText(textField.key.hintRes),
                    keyboardOptions = textField.key.getKeyboardOptions(),
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onSaveClick(context, saveAccount),
            enabled = state.textFields.values.all { it.errorState == NO_ERROR },
        ) {
            Text(stringResource(id = R.string.button_save_account))
        }
    }
}

private fun TextFieldName.getKeyboardOptions() = KeyboardOptions(
    imeAction = when (this) {
        REPEAT_PASSWORD -> ImeAction.Done
        else -> ImeAction.Next
    }
)

@ReadOnlyComposable
@Composable
private fun ErrorState.getErrorText(@StringRes hintRes: Int) =
    when (this) {
        NO_ERROR -> ""
        TEXT_EMPTY -> stringResource(this.errorRes!!, stringResource(hintRes))
        PASSWORDS_MUST_MATCH -> stringResource(this.errorRes!!)
    }

private fun onSaveClick(
    context: Context,
    saveAccount: suspend () -> Boolean,
): () -> Unit {
    return {
        CoroutineScope(IO).launch {
            val isInserted = saveAccount()

            withContext(Main) {
                context.showToast(
                    if (isInserted) {
                        R.string.success_save_account
                    } else {
                        R.string.error_save_unsuccessful
                    }
                )
            }
        }
    }
}

@ScreenPreviews
@Composable
private fun PreviewSaveAccountScreen() = PassVaultTheme {
    SaveAccountContent(
        state = SaveAccountState(
            mapOf(
                PLATFORM to TextFieldState(),
                USERNAME to TextFieldState(),
                PASSWORD to TextFieldState(),
                REPEAT_PASSWORD to TextFieldState(),
            )
        ),
        onTextChange = { _, _ -> },
        saveAccount = suspend { true },
        validate = {},
    )
}

@ScreenPreviews
@Composable
private fun PreviewSaveAccountScreenNoErrors() = PassVaultTheme {
    SaveAccountContent(
        state = SaveAccountState(
            mapOf(
                PLATFORM to TextFieldState(text = "platform", errorState = NO_ERROR),
                USERNAME to TextFieldState(text = "username", errorState = NO_ERROR),
                PASSWORD to TextFieldState(text = "password", errorState = NO_ERROR),
                REPEAT_PASSWORD to TextFieldState(text = "password", errorState = NO_ERROR),
            )
        ),
        onTextChange = { _, _ -> },
        saveAccount = suspend { true },
        validate = {},
    )
}
