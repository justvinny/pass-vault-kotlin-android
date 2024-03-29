package com.vinsonb.password.manager.kotlin.ui.features.topnavmenu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.vinsonb.password.manager.kotlin.R
import com.vinsonb.password.manager.kotlin.ui.features.credits.CreditsDialog
import com.vinsonb.password.manager.kotlin.ui.theme.PassVaultTheme
import com.vinsonb.password.manager.kotlin.utilities.ComponentPreviews

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavMenu(
    title: String,
    isMenuVisible: Boolean,
    importCsv: () -> Unit,
    exportCsv: () -> Unit,
    logout: () -> Unit,
) {
    val isCreditsDialogVisible = rememberSaveable { mutableStateOf(false) }
    DialogHandler(isDialogVisible = isCreditsDialogVisible)

    val topNavMenuItems = remember {
        arrayOf(
            TopNavMenuItem(
                itemNameRes = R.string.menu_item_import_csv,
                action = importCsv,
            ),
            TopNavMenuItem(
                itemNameRes = R.string.menu_item_export_csv,
                action = exportCsv,
            ),
            TopNavMenuItem(
                itemNameRes = R.string.menu_item_credits,
                action = { isCreditsDialogVisible.value = true },
            ),
            TopNavMenuItem(
                itemNameRes = R.string.menu_item_logout,
                action = logout,
            ),
        )
    }
    var isMenuExpanded by rememberSaveable { mutableStateOf(false) }
    TopAppBar(
        title = {
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = title,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        actions = {
            if (isMenuVisible) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = { isMenuExpanded = !isMenuExpanded }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = stringResource(id = R.string.content_menu),
                        )
                    }

                    val dismissMenu = { isMenuExpanded = false }
                    DropdownMenu(
                        expanded = isMenuExpanded,
                        onDismissRequest = dismissMenu,
                    ) {
                        topNavMenuItems.forEach { menuItem ->
                            DropdownMenuItem(
                                text = { Text(stringResource(id = menuItem.itemNameRes)) },
                                onClick = {
                                    menuItem.action()
                                    dismissMenu()
                                },
                            )
                        }
                    }
                }
            }
        },
    )
}

@Composable
private fun DialogHandler(
    isDialogVisible: MutableState<Boolean>,
) {
    if (isDialogVisible.value) {
        CreditsDialog { isDialogVisible.value = false }
    }
}

@ComponentPreviews
@Composable
private fun PreviewTopNavMenu() = PassVaultTheme {
    TopNavMenu(
        title = stringResource(id = R.string.app_name),
        isMenuVisible = true,
        importCsv = {},
        exportCsv = {},
        logout = {},
    )
}
