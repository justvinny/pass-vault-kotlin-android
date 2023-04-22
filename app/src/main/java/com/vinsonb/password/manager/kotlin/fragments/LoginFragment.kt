package com.vinsonb.password.manager.kotlin.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.vinsonb.password.manager.kotlin.R
import com.vinsonb.password.manager.kotlin.extensions.showToast
import com.vinsonb.password.manager.kotlin.ui.features.forgotpasscode.ForgotPasscodeDialog
import com.vinsonb.password.manager.kotlin.ui.features.forgotpasscode.ForgotPasscodeState
import com.vinsonb.password.manager.kotlin.ui.features.forgotpasscode.ForgotPasscodeViewModel
import com.vinsonb.password.manager.kotlin.ui.features.login.LoginScreen
import com.vinsonb.password.manager.kotlin.ui.features.login.LoginState.Companion.MAX_PASSCODE_DIGITS
import com.vinsonb.password.manager.kotlin.ui.features.login.LoginViewModel
import com.vinsonb.password.manager.kotlin.utilities.Constants.Password.SharedPreferenceKeys.AUTHENTICATED_KEY
import com.vinsonb.password.manager.kotlin.utilities.Constants.Password.SharedPreferenceKeys.PASSCODE_KEY
import com.vinsonb.password.manager.kotlin.utilities.Constants.Password.SharedPreferenceKeys.SECRET_ANSWER_KEY
import com.vinsonb.password.manager.kotlin.utilities.Constants.Password.SharedPreferenceKeys.SECRET_QUESTION_KEY
import com.vinsonb.password.manager.kotlin.utilities.withComposeView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private val loginViewModel: LoginViewModel by viewModels()
    private val forgotPasscodeViewModel by lazy {
        ForgotPasscodeViewModel(
            savedSecretAnswer = getSecretAnswer(),
            saveNewPasscode = this::saveNewPasscode,
            showSucceededToast = { requireContext().showToast(R.string.success_passcode_reset) },
            showFailedToast = { requireContext().showToast(R.string.error_reset_unsuccessful) },
        )
    }
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        if (isAccountNotCreated()) {
            findNavController().navigate(R.id.create_login_fragment)
        }

        lifecycleScope.launch {
            loginViewModel.stateFlow.collect { state ->
                if (state.passcodeLength == MAX_PASSCODE_DIGITS) {
                    loginViewModel.onClearAllDigits()

                    if (passcodeMatches(state.passcode) && login()) {
                        findNavController().popBackStack()
                        findNavController().navigate(R.id.view_accounts_fragment)
                    } else {
                        requireContext().showToast(R.string.error_wrong_passcode)
                    }
                }
            }
        }

        return withComposeView(requireContext()) {
            LoginScreen(loginViewModel, forgotPasscodeViewModel::showDialog)

            val isDialogShown by forgotPasscodeViewModel.stateFlow.collectAsState()
            if (isDialogShown is ForgotPasscodeState.Visible) {
                ForgotPasscodeDialog(viewModel = forgotPasscodeViewModel)
            }
        }
    }

    /**
     * Check if account is not already created by checking SharedPreferences if
     * values: passcode, secret question, and secret answer exist.
     *
     * returns whether the account is created or not
     */
    private fun isAccountNotCreated(): Boolean {
        val passcode = sharedPreferences.getString(PASSCODE_KEY, "")
        val secretQuestion = sharedPreferences.getString(SECRET_QUESTION_KEY, "")
        val secretAnswer = sharedPreferences.getString(SECRET_ANSWER_KEY, "")
        return passcode.isNullOrBlank() && secretQuestion.isNullOrBlank() && secretAnswer.isNullOrBlank()
    }

    private fun passcodeMatches(passcode: String): Boolean {
        return sharedPreferences.getString(PASSCODE_KEY, "") == passcode
    }

    private fun login(): Boolean {
        with(sharedPreferences.edit()) {
            putBoolean(AUTHENTICATED_KEY, true)
            return commit()
        }
    }

    private fun getSecretAnswer(): String {
        return sharedPreferences.getString(SECRET_ANSWER_KEY, "") ?: ""
    }

    private fun saveNewPasscode(newPasscode: String): Boolean {
        with(sharedPreferences.edit()) {
            putString(PASSCODE_KEY, newPasscode)
            return commit()
        }
    }
}
