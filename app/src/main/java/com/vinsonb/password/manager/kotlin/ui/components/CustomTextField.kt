package com.vinsonb.password.manager.kotlin.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
        emptyErrorTextPlaceHolder: @Composable () -> Unit = { VerticalSpacer() },
        keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    ) {
        CustomTextField(
            modifier = modifier,
            text = text,
            onTextChange = onTextChange,
            label = label,
            isError = isError,
            errorText = errorText,
            emptyErrorTextPlaceHolder = emptyErrorTextPlaceHolder,
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
        emptyErrorTextPlaceHolder: @Composable () -> Unit = { VerticalSpacer() },
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
            emptyErrorTextPlaceHolder = emptyErrorTextPlaceHolder,
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

    @Composable
    fun Search(
        modifier: Modifier = Modifier,
        text: String = "",
        onTextChange: (String) -> Unit,
        label: String = "",
        onSearch: () -> Unit,
    ) {
        CustomTextField(
            modifier = modifier,
            text = text,
            onTextChange = onTextChange,
            label = label,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = stringResource(id = R.string.content_search),
                )
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                onSearch()
            })
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
    emptyErrorTextPlaceHolder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
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
                if (emptyErrorTextPlaceHolder != null) {
                    emptyErrorTextPlaceHolder()
                }
            }
        },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
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

@ComponentPreviews
@Composable
private fun PreviewTextFieldSearch() = PassVaultTheme {
    CustomTextField.Search(
        label = "Search",
        onTextChange = {},
        onSearch = {},
    )
}
