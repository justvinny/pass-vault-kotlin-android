package com.vinsonb.password.manager.kotlin.ui.features.credits

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.vinsonb.password.manager.kotlin.R
import com.vinsonb.password.manager.kotlin.extensions.openWebPage
import com.vinsonb.password.manager.kotlin.ui.theme.PassVaultTheme
import com.vinsonb.password.manager.kotlin.utilities.ScreenPreviews

@Composable
fun CreditsDialog(
    creditsList: List<Credit> = CREDITS_DATA,
    dismissDialog: () -> Unit,
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = dismissDialog) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Text(
                modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                text = stringResource(id = R.string.menu_item_credits),
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = MaterialTheme.colorScheme.primary,
                )
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 0.dp),
            ) {
                items(creditsList) {
                    CreditsItem(
                        onOpenWebPage = { context.openWebPage(it.url) },
                        creditTitle = it.title,
                    )
                }
            }
        }
    }
}

@Composable
private fun CreditsItem(
    onOpenWebPage: () -> Unit,
    creditTitle: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
    ) {
        IconButton(onClick = onOpenWebPage) {
            Icon(
                imageVector = Icons.Outlined.Link,
                contentDescription = stringResource(id = R.string.content_open_link),
                tint = MaterialTheme.colorScheme.primary,
            )
        }

        Text(creditTitle)
    }
}

@ScreenPreviews
@Composable
fun PreviewCreditsDialog() = PassVaultTheme {
    CreditsDialog(dismissDialog = {})
}
