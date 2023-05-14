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
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vinsonb.password.manager.kotlin.R
import com.vinsonb.password.manager.kotlin.extensions.showToast
import com.vinsonb.password.manager.kotlin.ui.components.HorizontalFillSpacer
import com.vinsonb.password.manager.kotlin.ui.components.VerticalFillSpacer
import com.vinsonb.password.manager.kotlin.ui.features.forgotpasscode.ForgotPasscodeDialog
import com.vinsonb.password.manager.kotlin.ui.features.forgotpasscode.ForgotPasscodeViewModel
import com.vinsonb.password.manager.kotlin.ui.features.login.LoginState.Companion.BUTTON_GROUP_ROW_COUNT
import com.vinsonb.password.manager.kotlin.ui.features.login.LoginState.Companion.BUTTON_GROUP_ROW_N_ITEMS
import com.vinsonb.password.manager.kotlin.ui.features.login.LoginState.Companion.BUTTON_GROUP_ROW_N_ITEMS_LAST_INDEX
import com.vinsonb.password.manager.kotlin.ui.features.login.LoginState.Companion.BUTTON_LABEL_LIST
import com.vinsonb.password.manager.kotlin.ui.theme.PassVaultTheme
import com.vinsonb.password.manager.kotlin.utilities.Constants.Password.PASSCODE_MAX_LENGTH
import com.vinsonb.password.manager.kotlin.utilities.ScreenPreviews
import com.vinsonb.password.manager.kotlin.utilities.SimpleToastEvent

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel,
    forgotPasscodeViewModel: ForgotPasscodeViewModel,
    secretQuestion: String,
    showToastFromActivity: (Int) -> Unit,
    passcodeMatches: (String) -> Boolean,
    login: () -> Unit,
) {
    val state by loginViewModel.stateFlow.collectAsState()
    val forgotPasscodeToastState by forgotPasscodeViewModel.eventFlow.collectAsState(
        SimpleToastEvent.None
    )
    val isDialogVisible = rememberSaveable { mutableStateOf(false) }

    ToastHandler(
        loginState = state,
        forgotPasscodeToastState = forgotPasscodeToastState,
        showToastFromActivity = showToastFromActivity,
        passcodeMatches = passcodeMatches,
        login = login,
        onClearAllDigits = loginViewModel::onClearAllDigits,
        dismissDialog = { isDialogVisible.value = false },
        resetForgotPasscodeToastState = { forgotPasscodeViewModel.sendEvent(SimpleToastEvent.None) }
    )

    ForgotPasscodeDialogHandler(
        isDialogVisible = isDialogVisible,
        forgotPasscodeViewModel = forgotPasscodeViewModel,
        secretQuestion = secretQuestion,
    )

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
            onEnterPasscodeDigit = loginViewModel::onEnterPasscodeDigit,
            onClearLastDigit = loginViewModel::onClearLastDigit,
            showForgotPasswordDialog = { isDialogVisible.value = true },
        )
    }
}

@Composable
private fun ToastHandler(
    loginState: LoginState,
    forgotPasscodeToastState: SimpleToastEvent,
    showToastFromActivity: (Int) -> Unit,
    passcodeMatches: (String) -> Boolean,
    login: () -> Unit,
    onClearAllDigits: () -> Unit,
    dismissDialog: () -> Unit,
    resetForgotPasscodeToastState: () -> Unit,
) {
    LaunchedEffect(loginState) {
        if (loginState.passcodeLength == PASSCODE_MAX_LENGTH) {
            if (passcodeMatches(loginState.passcode)) {
                login()
            } else {
                showToastFromActivity(R.string.error_wrong_passcode)
            }

            onClearAllDigits()
        }
    }

    val context = LocalContext.current
    LaunchedEffect(forgotPasscodeToastState) {
        when (forgotPasscodeToastState) {
            SimpleToastEvent.None -> {}
            SimpleToastEvent.ShowFailed ->
                context.showToast(R.string.error_reset_unsuccessful)
            SimpleToastEvent.ShowSucceeded -> {
                dismissDialog()
                context.showToast(R.string.success_passcode_reset)
            }
        }
        resetForgotPasscodeToastState()
    }
}

@Composable
private fun ForgotPasscodeDialogHandler(
    isDialogVisible: MutableState<Boolean>,
    forgotPasscodeViewModel: ForgotPasscodeViewModel,
    secretQuestion: String,
) {
    val state by forgotPasscodeViewModel.stateFlow.collectAsState()

    if (isDialogVisible.value) {
        ForgotPasscodeDialog(
            state = state,
            resetPasscode = forgotPasscodeViewModel::resetPasscode,
            validateSecretAnswer = forgotPasscodeViewModel::validateSecretAnswer,
            validatePasscode = forgotPasscodeViewModel::validatePasscode,
            validateRepeatPasscode = forgotPasscodeViewModel::validateRepeatPasscode,
            secretQuestion = secretQuestion,
        ) {
            isDialogVisible.value = false
        }
    }
}

@Composable
private fun PasscodeCircleGroup(loginState: LoginState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        repeat(PASSCODE_MAX_LENGTH) { index ->
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
        loginViewModel = LoginViewModel(rememberCoroutineScope()),
        forgotPasscodeViewModel = ForgotPasscodeViewModel(
            rememberCoroutineScope(),
            "secretAnswer",
            saveNewPasscode = { true },
        ),
        secretQuestion = "secretQuestion",
        showToastFromActivity = {},
        passcodeMatches = { true },
        login = {},
    )
}
