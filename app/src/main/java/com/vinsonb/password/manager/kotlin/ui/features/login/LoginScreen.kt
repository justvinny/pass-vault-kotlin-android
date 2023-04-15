package com.vinsonb.password.manager.kotlin.ui.features.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vinsonb.password.manager.kotlin.R
import com.vinsonb.password.manager.kotlin.ui.components.HorizontalFillSpacer
import com.vinsonb.password.manager.kotlin.ui.components.VerticalFillSpacer
import com.vinsonb.password.manager.kotlin.ui.features.login.LoginState.Companion.BUTTON_GROUP_ROW_COUNT
import com.vinsonb.password.manager.kotlin.ui.features.login.LoginState.Companion.BUTTON_GROUP_ROW_N_ITEMS
import com.vinsonb.password.manager.kotlin.ui.features.login.LoginState.Companion.BUTTON_GROUP_ROW_N_ITEMS_LAST_INDEX
import com.vinsonb.password.manager.kotlin.ui.features.login.LoginState.Companion.BUTTON_LABEL_LIST
import com.vinsonb.password.manager.kotlin.ui.features.login.LoginState.Companion.MAX_PASSCODE_DIGITS
import com.vinsonb.password.manager.kotlin.ui.theme.PassVaultTheme
import com.vinsonb.password.manager.kotlin.utilities.ScreenPreviews

@Composable
fun LoginScreen(viewModel: LoginViewModel, showForgotPasswordDialog: () -> Unit) {
    val state by viewModel.stateFlow.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
    ) {
        VerticalFillSpacer()
        Image(
            modifier = Modifier.widthIn(max = 350.dp),
            painter = painterResource(id = R.drawable.undraw_secure_login_pdn4),
            contentDescription = null,
        )
        VerticalFillSpacer()
        PasscodeCircleGroup(state)
        PasscodeButtonGroup(
            onEnterPasscodeDigit = viewModel::onEnterPasscodeDigit,
            onClearLastDigit = viewModel::onClearLastDigit,
            showForgotPasswordDialog = showForgotPasswordDialog,
        )
    }
}

@Composable
private fun PasscodeCircleGroup(loginState: LoginState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        repeat(MAX_PASSCODE_DIGITS) { index ->
            val nonZeroIndex = index + 1
            Icon(
                modifier = Modifier.padding(vertical = 32.dp, horizontal = 16.dp),
                painter = painterResource(
                    id = when {
                        nonZeroIndex > loginState.passcodeLength -> R.drawable.ic_circle_outlined
                        else -> R.drawable.ic_circle_filled
                    },
                ),
                contentDescription = stringResource(
                    id = when {
                        nonZeroIndex > loginState.passcodeLength -> R.string.content_passcode_digit_circle_empty
                        else -> R.string.content_passcode_digit_circle_filled
                    },
                    nonZeroIndex,
                ),
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun PasscodeButtonGroup(
    onEnterPasscodeDigit: (Int) -> Unit,
    onClearLastDigit: () -> Unit,
    showForgotPasswordDialog: () -> Unit,
) {
    val buttonLabelsQueue = ArrayDeque(BUTTON_LABEL_LIST)

    repeat(BUTTON_GROUP_ROW_COUNT) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
        ) {
            repeat(BUTTON_GROUP_ROW_N_ITEMS) { innerIndex ->
                buttonLabelsQueue.removeFirstOrNull()?.let { btnStringRes ->
                    val btnLabel = stringResource(id = btnStringRes)

                    OutlinedButton(
                        modifier = Modifier.weight(4f),
                        contentPadding = PaddingValues(vertical = 16.dp),
                        border = BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary,
                        ),
                        onClick = {
                            when (btnStringRes) {
                                R.string.button_forgot_password -> showForgotPasswordDialog()
                                R.string.button_clear -> onClearLastDigit()
                                else -> onEnterPasscodeDigit(btnLabel.toInt())
                            }
                        },
                    ) {
                        Text(btnLabel)
                    }

                    if (innerIndex < BUTTON_GROUP_ROW_N_ITEMS_LAST_INDEX) {
                        HorizontalFillSpacer()
                    }
                }
            }
        }
    }
}

@ScreenPreviews
@Composable
private fun PreviewLoginScreen() = PassVaultTheme {
    LoginScreen(
        viewModel = LoginViewModel(rememberCoroutineScope()),
        showForgotPasswordDialog = {},
    )
}
