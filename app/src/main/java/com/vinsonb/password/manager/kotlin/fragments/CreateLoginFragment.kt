package com.vinsonb.password.manager.kotlin.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.vinsonb.password.manager.kotlin.R
import com.vinsonb.password.manager.kotlin.di.CoroutineModule
import com.vinsonb.password.manager.kotlin.ui.features.createlogin.CreateLoginScreen
import com.vinsonb.password.manager.kotlin.ui.features.createlogin.CreateLoginViewModel
import com.vinsonb.password.manager.kotlin.ui.theme.PassVaultTheme
import com.vinsonb.password.manager.kotlin.utilities.Constants.Password.SharedPreferenceKeys.PASSCODE_KEY
import com.vinsonb.password.manager.kotlin.utilities.Constants.Password.SharedPreferenceKeys.SECRET_ANSWER_KEY
import com.vinsonb.password.manager.kotlin.utilities.Constants.Password.SharedPreferenceKeys.SECRET_QUESTION_KEY
import com.vinsonb.password.manager.kotlin.utilities.withComposeView
import kotlinx.coroutines.CoroutineScope

class CreateLoginFragment : Fragment() {
    private lateinit var sharedPreferences: SharedPreferences
    private val viewModel by lazy {
        CreateLoginViewModel(
            scope = CoroutineScope(CoroutineModule.providesCoroutineDispatchers().default),
            _createLogin = this::saveData,
        )
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        return withComposeView(requireContext()) {
            PassVaultTheme {
                CreateLoginScreen(viewModel = viewModel)
            }
        }
    }

    private fun saveData(
        passcode: String,
        secretQuestion: String,
        secretAnswer: String,
    ) {
        with(sharedPreferences.edit()) {
            putString(PASSCODE_KEY, passcode)
            putString(SECRET_QUESTION_KEY, secretQuestion)
            putString(SECRET_ANSWER_KEY, secretAnswer)
            if (commit()) {
                findNavController().popBackStack()
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.error_save_unsuccessful),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
