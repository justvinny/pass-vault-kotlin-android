package com.vinsonb.password.manager.kotlin.ui.features.viewaccount

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.vinsonb.password.manager.kotlin.R
import com.vinsonb.password.manager.kotlin.database.enitities.Account
import com.vinsonb.password.manager.kotlin.extensions.showToast
import com.vinsonb.password.manager.kotlin.ui.components.CustomTextField
import com.vinsonb.password.manager.kotlin.ui.theme.PassVaultTheme
import com.vinsonb.password.manager.kotlin.utilities.ScreenPreviews
import com.vinsonb.password.manager.kotlin.utilities.TextResIdProvider

@Composable
fun ViewAccountScreen(viewModel: ViewAccountViewModel) {
    val toastState by viewModel.eventFlow.collectAsState(ViewAccountToastState.None)
    val isDialogVisible = rememberSaveable { mutableStateOf(false) }
    ToastHandler(
        toastState = toastState,
        dismissDialog = { isDialogVisible.value = false },
        resetToastState = { viewModel.sendEvent(ViewAccountToastState.None) },
    )

    val state by viewModel.stateFlow.collectAsState()
    ViewAccountContent(
        state = state,
        isDialogVisible = isDialogVisible,
        onSearch = viewModel::onSearch,
        onSelectAccount = viewModel::onSelectAccount,
        onUpdate = viewModel::onUpdateAccount,
        onDelete = viewModel::onDeleteAccount,
        onClearSearch = viewModel::onClearSearch,
    )
}

@Composable
private fun ToastHandler(
    toastState: ViewAccountToastState,
    dismissDialog: () -> Unit,
    resetToastState: () -> Unit,
) {
    val context = LocalContext.current

    LaunchedEffect(toastState) {
        when (toastState) {
            ViewAccountToastState.None -> {}
            else -> {
                if (toastState is ViewAccountToastState.SuccessfullyDeleted) {
                    dismissDialog()
                }

                context.showToast((toastState as TextResIdProvider).getTextResId())
            }
        }
        resetToastState()
    }
}

@Composable
private fun ViewAccountContent(
    state: ViewAccountState,
    isDialogVisible: MutableState<Boolean>,
    onSearch: (String) -> Unit,
    onSelectAccount: (Account) -> Unit,
    onUpdate: (Account, Account) -> Unit,
    onDelete: (Account) -> Unit,
    onClearSearch: () -> Unit,
) {

    ViewAccountDialogHandler(
        state = state,
        isDialogVisible = isDialogVisible,
        onUpdate = onUpdate,
        onDelete = onDelete,
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            .imePadding()
            .navigationBarsPadding()
            .statusBarsPadding(),
    ) {
        var searchQuery by rememberSaveable { mutableStateOf("") }

        CustomTextField.Search(
            modifier = Modifier.padding(bottom = 16.dp),
            text = searchQuery,
            label = stringResource(id = R.string.hint_search),
            trailingIcon = {
                IconButton(onClick = {
                    searchQuery = ""
                    onClearSearch()
                }) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = stringResource(id = R.string.content_clear_search),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            },
            onTextChange = {
                searchQuery = it
                onSearch(it)
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            items(state.accounts) {
                ViewAccountItem(
                    account = it,
                    selectAccount = {
                        onSelectAccount(it)
                        isDialogVisible.value = true
                    }
                )
            }
        }
    }
}

@Composable
private fun ViewAccountDialogHandler(
    state: ViewAccountState,
    isDialogVisible: MutableState<Boolean>,
    onUpdate: (Account, Account) -> Unit,
    onDelete: (Account) -> Unit,
) {
    if (isDialogVisible.value) {
        state.selectedAccount?.let {
            ViewAccountItemDialog(
                account = it,
                onUpdate = onUpdate,
                onDelete = onDelete,
            ) { isDialogVisible.value = false }
        }
    }
}

@ScreenPreviews
@Composable
private fun PreviewViewAccountScreen() = PassVaultTheme {
    val isDialogVisible = remember { mutableStateOf(false) }
    ViewAccountContent(
        state = ViewAccountState(
            listOf(
                Account("Platform", "Username@email.com", "fnalsnvfdsnvlsdn"),
                Account("Platform1", "Username1@email.com", "testestsvfd"),
                Account("Platform2", "Username2@email.com", "bfzbxdfbngdfxafaf"),
            )
        ),
        isDialogVisible = isDialogVisible,
        onSearch = {},
        onSelectAccount = {},
        onUpdate = { _, _ -> },
        onDelete = {},
        onClearSearch = {},
    )
}
