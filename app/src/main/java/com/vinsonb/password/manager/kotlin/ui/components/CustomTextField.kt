package com.vinsonb.password.manager.kotlin.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.vinsonb.password.manager.kotlin.R
import com.vinsonb.password.manager.kotlin.ui.theme.PassVaultTheme
import com.vinsonb.password.manager.kotlin.utilities.ComponentPreviews

object CustomTextField {
    @Composable
    fun Normal(
        modifier: Modifier = Modifier,
        text: String = "",
        onTextChange: (String) -> Unit,
        label: String = "",
        isError: Boolean = false,
        errorText: String = "",
        keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    ) {
        CustomTextField(
            modifier = modifier,
            text = text,
            onTextChange = onTextChange,
            label = label,
            isError = isError,
            errorText = errorText,
            visualTransformation = VisualTransformation.None,
            keyboardOptions = keyboardOptions,
        )
    }

    @Composable
    fun Password(
        modifier: Modifier = Modifier,
        text: String = "",
        onTextChange: (String) -> Unit,
        label: String = "",
        isError: Boolean = false,
        errorText: String = "",
        keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
        isPasswordVisibleDefault: Boolean = false,
    ) {
        val isPasswordVisible = rememberSaveable { mutableStateOf(isPasswordVisibleDefault) }

        CustomTextField(
            modifier = modifier,
            text = text,
            onTextChange = onTextChange,
            label = label,
            isError = isError,
            errorText = errorText,
            trailingIcon = {
                if (isPasswordVisible.value) {
                    IconButton(onClick = { isPasswordVisible.value = false }) {
                        Icon(
                            imageVector = Icons.Filled.VisibilityOff,
                            contentDescription = stringResource(id = R.string.content_hide_password),
                        )
                    }
                } else {
                    IconButton(onClick = { isPasswordVisible.value = true }) {
                        Icon(
                            imageVector = Icons.Filled.Visibility,
                            contentDescription = stringResource(id = R.string.content_show_password),
                        )
                    }
                }
            },
            visualTransformation = if (isPasswordVisible.value) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            keyboardOptions = keyboardOptions,
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomTextField(
    modifier: Modifier = Modifier,
    text: String = "",
    onTextChange: (String) -> Unit,
    label: String = "",
    isError: Boolean = false,
    errorText: String = "",
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        value = text,
        onValueChange = onTextChange,
        label = { Text(label) },
        isError = isError,
        supportingText = {
            if (isError) {
                Text(errorText)
            } else {
                Spacer(Modifier.height(16.dp))
            }
        },
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
    )
}

@ComponentPreviews
@Composable
private fun PreviewTextField() = PassVaultTheme {
    CustomTextField(text = "Text", onTextChange = { }, label = "Label")
}

@ComponentPreviews
@Composable
private fun PreviewTextFieldError() = PassVaultTheme {
    CustomTextField(
        text = "Text",
        onTextChange = { },
        label = "Label",
        isError = true,
        errorText = "Error Text",
    )
}

@ComponentPreviews
@Composable
private fun PreviewTextFieldPasswordVisible() = PassVaultTheme {
    CustomTextField.Password(
        text = "Text",
        onTextChange = { },
        label = "Label",
        errorText = "Error Text",
        isPasswordVisibleDefault = true,
    )
}

@ComponentPreviews
@Composable
private fun PreviewTextFieldPasswordHidden() = PassVaultTheme {
    CustomTextField.Password(
        text = "Text",
        onTextChange = { },
        label = "Label",
        errorText = "Error Text",
    )
}
