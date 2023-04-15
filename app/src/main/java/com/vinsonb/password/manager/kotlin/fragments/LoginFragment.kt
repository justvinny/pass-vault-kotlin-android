package com.vinsonb.password.manager.kotlin.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.vinsonb.password.manager.kotlin.R
import com.vinsonb.password.manager.kotlin.extensions.showToast
import com.vinsonb.password.manager.kotlin.fragments.dialogs.ForgotPasswordDialog
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
    private val viewModel: LoginViewModel by viewModels()
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
            viewModel.stateFlow.collect { state ->
                if (state.passcodeLength == MAX_PASSCODE_DIGITS) {
                    viewModel.onClearAllDigits()

                    if (passcodeMatches(state.passcode)) {
                        findNavController().popBackStack()
                        findNavController().navigate(R.id.view_accounts_fragment)
                    } else {
                        requireContext().showToast(R.string.error_passcode_must_match)
                    }
                }
            }
        }

        return withComposeView(requireContext()) {
            LoginScreen(viewModel, this::showForgotPasswordDialog)
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

    /**
     * Logs in the user by changing the authentication value in SharedPreferences to true if
     * the passcode entered matches the value stored.
     *
     * returns whether the user successfully logged in or not.
     */
    private fun passcodeMatches(passcode: String): Boolean {
        val savedPasscode = sharedPreferences.getString(PASSCODE_KEY, "")
        if (savedPasscode == passcode) {
            with(sharedPreferences.edit()) {
                putBoolean(AUTHENTICATED_KEY, true)

                return commit()
            }
        }
        return false
    }

    private fun showForgotPasswordDialog() {
        ForgotPasswordDialog().show(parentFragmentManager, ForgotPasswordDialog.TAG)
    }
}