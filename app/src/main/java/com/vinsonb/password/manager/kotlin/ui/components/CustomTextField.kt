package com.vinsonb.password.manager.kotlin.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    text: String = "",
    onTextChange: (String) -> Unit,
    label: String = "",
    isError: Boolean = false,
    errorText: String = "",
) {
    androidx.compose.material3.TextField(
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
            }
        }
    )
}

@Preview
@Composable
private fun PreviewTextField() {
    CustomTextField(text = "Text", onTextChange = { }, label = "Label")
}

@Preview
@Composable
private fun PreviewTextFieldError() {
    CustomTextField(
        text = "Text",
        onTextChange = { },
        label = "Label",
        isError = true,
        errorText = "Error Text",
    )
}