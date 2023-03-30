package com.vinsonb.password.manager.kotlin.ui.viewaccount

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.vinsonb.password.manager.kotlin.R
import com.vinsonb.password.manager.kotlin.database.enitities.Account
import com.vinsonb.password.manager.kotlin.ui.theme.PassVaultTheme
import com.vinsonb.password.manager.kotlin.utilities.ClipboardUtilities
import com.vinsonb.password.manager.kotlin.utilities.ComponentPreviews

@Composable
fun ViewAccountItem(
    context: Context,
    account: Account,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
    ) {
        ConstraintLayout(
            modifier = Modifier
                .height(150.dp)
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            val (
                platformIcon,
                platform,
                usernameIcon,
                username,
                expandIcon,
                copyIcon,
            ) = createRefs()

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .constrainAs(platformIcon) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)

                    },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    modifier = Modifier.size(32.dp),
                    painter = painterResource(id = R.drawable.ic_platform),
                    contentDescription = stringResource(id = R.string.content_platform_icon),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            Text(
                modifier = Modifier
                    .constrainAs(platform) {
                        top.linkTo(platformIcon.top)
                        bottom.linkTo(platformIcon.bottom)
                        start.linkTo(platformIcon.end)
                        end.linkTo(expandIcon.start)
                        width = Dimension.fillToConstraints
                    },
                text = account.platform,
                overflow = TextOverflow.Ellipsis,
                maxLines = 3,
            )

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .constrainAs(usernameIcon) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    modifier = Modifier.size(32.dp),
                    imageVector = Icons.Filled.Person,
                    contentDescription = stringResource(id = R.string.content_username_icon),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            Text(
                modifier = Modifier
                    .constrainAs(username) {
                        top.linkTo(usernameIcon.top)
                        bottom.linkTo(usernameIcon.bottom)
                        start.linkTo(usernameIcon.end)
                        end.linkTo(copyIcon.start)
                        width = Dimension.fillToConstraints
                    },
                text = account.username,
                overflow = TextOverflow.Ellipsis,
                maxLines = 3,
            )

            // TODO: Migrate AccountDialog and integrate it with this Composable.
            var isDialogVisible by remember { mutableStateOf(false) }

            if (isDialogVisible) {
                ViewAccountItemDialog { isDialogVisible = false }
            }

            IconButton(
                modifier = Modifier
                    .size(48.dp)
                    .constrainAs(expandIcon) {
                        top.linkTo(platformIcon.top)
                        bottom.linkTo(platformIcon.bottom)
                        end.linkTo(parent.end)
                    },
                onClick = { isDialogVisible = true },
            ) {
                Icon(
                    modifier = Modifier.size(40.dp),
                    imageVector = Icons.Filled.Fullscreen,
                    contentDescription = stringResource(id = R.string.content_maximize_account),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }

            IconButton(
                modifier = Modifier
                    .size(48.dp)
                    .constrainAs(copyIcon) {
                        top.linkTo(usernameIcon.top)
                        bottom.linkTo(usernameIcon.bottom)
                        end.linkTo(parent.end)
                    },
                onClick = {
                    ClipboardUtilities.copyToClipboard(
                        context = context,
                        clipLabel = ClipboardUtilities.CLIP_PASSWORD_LABEL,
                        toCopy = account.password,
                        username = account.username,
                    )
                },
            ) {
                Icon(
                    imageVector = Icons.Filled.ContentCopy,
                    contentDescription = stringResource(id = R.string.content_copy_password),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@ComponentPreviews
@Composable
fun PreviewViewAccountItem() = PassVaultTheme {
    ViewAccountItem(
        context = LocalContext.current,
        account = Account(
            platform = "Platform",
            username = "Username",
            password = "Password",
        ),
    )
}
