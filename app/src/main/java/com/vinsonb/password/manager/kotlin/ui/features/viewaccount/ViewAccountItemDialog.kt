package com.vinsonb.password.manager.kotlin.ui.features.viewaccount

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.vinsonb.password.manager.kotlin.R
import com.vinsonb.password.manager.kotlin.database.enitities.Account
import com.vinsonb.password.manager.kotlin.ui.components.CustomTextField
import com.vinsonb.password.manager.kotlin.ui.components.HorizontalFillSpacer
import com.vinsonb.password.manager.kotlin.ui.theme.PassVaultTheme
import com.vinsonb.password.manager.kotlin.utilities.ClipboardUtilities
import com.vinsonb.password.manager.kotlin.utilities.ComponentPreviews

@Composable
fun ViewAccountItemDialog(
    account: Account,
    onUpdate: (Account) -> Unit,
    onDelete: (Account) -> Unit,
    dismissDialog: () -> Unit,
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = dismissDialog) {
        Card(modifier = Modifier.fillMaxWidth()) {
            val isEditEnabled = rememberSaveable { mutableStateOf(false) }
            val password = rememberSaveable { mutableStateOf(account.password) }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = account.platform,
                    style = MaterialTheme.typography.headlineSmall
                        .copy(color = MaterialTheme.colorScheme.primary),
                )

                HorizontalFillSpacer()

                IconButton(
                    modifier = Modifier.offset(x = 12.dp),
                    onClick = {
                        if (isEditEnabled.value) {
                            onUpdate(
                                Account(
                                    platform = account.platform,
                                    username = account.username,
                                    password = password.value,
                                ),
                            )
                        }
                        isEditEnabled.value = !isEditEnabled.value
                    },
                ) {
                    isEditEnabled.GetSaveOrEditIcon()
                }

                IconButton(onClick = { onDelete(account) }) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = stringResource(id = R.string.content_delete_account),
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            }

            CustomTextField.Normal(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = account.username,
                onTextChange = {},
                enabled = false,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = stringResource(id = R.string.content_username_icon),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            ClipboardUtilities.copyToClipboard(
                                context = context,
                                clipLabel = ClipboardUtilities.CLIP_USERNAME_LABEL,
                                toCopy = account.username,
                            )
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ContentCopy,
                            contentDescription = stringResource(id = R.string.hint_password),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            )

            CustomTextField.Password(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                text = password.value,
                onTextChange = { password.value = it },
                enabled = isEditEnabled.value,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Key,
                        contentDescription = stringResource(id = R.string.hint_password),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            ClipboardUtilities.copyToClipboard(
                                context = context,
                                clipLabel = ClipboardUtilities.CLIP_PASSWORD_LABEL,
                                toCopy = password.value,
                                username = account.username,
                            )
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ContentCopy,
                            contentDescription = stringResource(id = R.string.hint_password),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            )
        }
    }
}

@Composable
private fun MutableState<Boolean>.GetSaveOrEditIcon() = when (this.value) {
    true -> Icon(
        imageVector = Icons.Filled.Save,
        contentDescription = stringResource(id = R.string.content_update_account),
        tint = MaterialTheme.colorScheme.primary,
    )
    else -> Icon(
        imageVector = Icons.Filled.Edit,
        contentDescription = stringResource(id = R.string.content_edit_account),
        tint = MaterialTheme.colorScheme.primary,
    )
}

@ComponentPreviews
@Composable
private fun PreviewViewAccountItemDialog() = PassVaultTheme {
    ViewAccountItemDialog(
        account = Account(
            platform = "Platform",
            username = "Username@email.com",
            password = "Password",
        ),
        onUpdate = {},
        onDelete = {},
        dismissDialog = {},
    )
}
