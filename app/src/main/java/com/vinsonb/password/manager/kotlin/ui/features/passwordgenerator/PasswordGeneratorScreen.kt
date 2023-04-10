package com.vinsonb.password.manager.kotlin.ui.features.passwordgenerator

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vinsonb.password.manager.kotlin.R
import com.vinsonb.password.manager.kotlin.ui.components.LabeledSwitch
import com.vinsonb.password.manager.kotlin.ui.theme.PassVaultTheme
import com.vinsonb.password.manager.kotlin.utilities.ClipboardUtilities
import com.vinsonb.password.manager.kotlin.utilities.PasswordGenerator
import com.vinsonb.password.manager.kotlin.utilities.ScreenPreviews

@Composable
fun PasswordGeneratorScreen() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .imePadding()
            .navigationBarsPadding(),
    ) {
        val rowModifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)

        val generatedPassword = rememberSaveable { mutableStateOf("") }
        GeneratedPasswordCopyField(
            modifier = rowModifier,
            generatedPassword = generatedPassword,
        )

        val passwordLength = rememberSaveable { mutableStateOf(PasswordGenerator.DEFAULT_LENGTH) }
        PasswordLengthSelector(
            modifier = rowModifier,
            passwordLength = passwordLength,
            getValidPasswordLength = PasswordGenerator::getValidPasswordLength,
        )

        val hasUppercase = rememberSaveable { mutableStateOf(true) }
        val hasLowercase = rememberSaveable { mutableStateOf(true) }
        val hasNumbers = rememberSaveable { mutableStateOf(false) }
        val hasSpecialCharacters = rememberSaveable { mutableStateOf(false) }
        LabeledSwitchGroup(
            modifier = rowModifier,
            hasUppercase = hasUppercase,
            hasLowercase = hasLowercase,
            hasNumbers = hasNumbers,
            hasSpecialCharacters = hasSpecialCharacters,
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                generatedPassword.value = PasswordGenerator.createPassword(
                    length = passwordLength.value,
                    hasUppercaseLetters = hasUppercase.value,
                    hasLowercaseLetters = hasLowercase.value,
                    hasNumbers = hasNumbers.value,
                    hasSpecialSymbols = hasSpecialCharacters.value,
                )
            },
        ) {
            Text(stringResource(id = R.string.button_generate_password))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GeneratedPasswordCopyField(
    modifier: Modifier,
    generatedPassword: MutableState<String>,
) {
    val context = LocalContext.current

    OutlinedTextField(
        modifier = modifier,
        value = generatedPassword.value,
        onValueChange = {},
        enabled = false,
        trailingIcon = {
            IconButton(
                onClick = {
                    ClipboardUtilities.copyToClipboard(
                        context = context,
                        clipLabel = ClipboardUtilities.CLIP_GENERATED_PASSWORD_LABEL,
                        toCopy = generatedPassword.value,
                    )
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.ContentCopy,
                    contentDescription = stringResource(id = R.string.content_copy_password),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            disabledBorderColor = MaterialTheme.colorScheme.primary,
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledTrailingIconColor = MaterialTheme.colorScheme.primary,
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PasswordLengthSelector(
    modifier: Modifier,
    passwordLength: MutableState<Int>,
    getValidPasswordLength: (Int) -> Int,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Slider(
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp),
            value = passwordLength.value.toFloat(),
            onValueChange = { passwordLength.value = it.toInt() },
            valueRange = PasswordGenerator.MIN_LENGTH.toFloat()..PasswordGenerator.MAX_LENGTH.toFloat(),
        )
        OutlinedTextField(
            modifier = Modifier.width(70.dp),
            value = TextFieldValue(
                text = passwordLength.value.toString(),
                selection = TextRange(passwordLength.value.toString().length),
            ),
            onValueChange = { newTextFieldValue ->
                passwordLength.value = newTextFieldValue.text.toIntOrNull()?.let {
                    getValidPasswordLength(it)
                } ?: PasswordGenerator.MIN_LENGTH
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done,
            ),
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
        )
    }
}

@Composable
private fun LabeledSwitchGroup(
    modifier: Modifier,
    hasUppercase: MutableState<Boolean>,
    hasLowercase: MutableState<Boolean>,
    hasNumbers: MutableState<Boolean>,
    hasSpecialCharacters: MutableState<Boolean>,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LabeledSwitch(
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp),
            label = stringResource(id = R.string.switch_uppercase),
            checked = hasUppercase.value,
            onCheckedChange = { hasUppercase.value = it },
        )
        LabeledSwitch(
            modifier = Modifier.weight(1f),
            label = stringResource(id = R.string.switch_lowercase),
            checked = hasLowercase.value,
            onCheckedChange = { hasLowercase.value = it },
        )
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LabeledSwitch(
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp),
            label = stringResource(id = R.string.switch_numbers),
            checked = hasNumbers.value,
            onCheckedChange = { hasNumbers.value = it },
        )
        LabeledSwitch(
            modifier = Modifier.weight(1f),
            label = stringResource(id = R.string.switch_special_symbols),
            checked = hasSpecialCharacters.value,
            onCheckedChange = { hasSpecialCharacters.value = it },
        )
    }
}

@ScreenPreviews
@Composable
private fun PreviewPasswordGeneratorScreen() = PassVaultTheme {
    PasswordGeneratorScreen()
}
