package com.vinsonb.password.manager.kotlin.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import com.vinsonb.password.manager.kotlin.R
import com.vinsonb.password.manager.kotlin.databinding.FragmentCreateLoginBinding
import com.vinsonb.password.manager.kotlin.utilities.Constants.Password.SharedPreferenceKeys.PASSCODE_KEY
import com.vinsonb.password.manager.kotlin.utilities.Constants.Password.SharedPreferenceKeys.SECRET_ANSWER_KEY
import com.vinsonb.password.manager.kotlin.utilities.Constants.Password.SharedPreferenceKeys.SECRET_QUESTION_KEY
import com.vinsonb.password.manager.kotlin.utilities.TextInputUtilities.checkInputNotEmpty
import com.vinsonb.password.manager.kotlin.utilities.TextInputUtilities.checkPasscodeLength
import com.vinsonb.password.manager.kotlin.utilities.TextInputUtilities.isNoneTextInputLayoutErrorEnabled
import com.vinsonb.password.manager.kotlin.utilities.TextInputUtilities.validateRepeatPasscode

private const val TAG = "CreateLoginFragment"

class CreateLoginFragment : Fragment(R.layout.fragment_create_login) {
    private lateinit var sharedPreferences: SharedPreferences
    private var _binding: FragmentCreateLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Init
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(view.context)
        enableAllErrorText()

        // Text Input Listeners
        binding.inputPasscode.addTextChangedListener {
            checkPasscodeLength(binding.layoutPasscode, getString(R.string.error_passcode_length))
            validateRepeatPasscode(
                binding.layoutRepeatPasscode,
                binding.inputPasscode,
                binding.inputRepeatPasscode,
                requireContext().getString(R.string.error_passcode_length),
                requireContext().getString(R.string.error_passcode_must_match)
            )
        }

        binding.inputRepeatPasscode.addTextChangedListener {
            validateRepeatPasscode(
                binding.layoutRepeatPasscode,
                binding.inputPasscode,
                binding.inputRepeatPasscode,
                requireContext().getString(R.string.error_passcode_length),
                requireContext().getString(R.string.error_passcode_must_match)
            )
        }

        binding.inputSecretQuestion.addTextChangedListener {
            checkInputNotEmpty(
                binding.layoutSecretQuestion,
                getString(R.string.error_text_empty, binding.layoutSecretQuestion.hint)
            )
        }

        binding.inputSecretAnswer.addTextChangedListener {
            checkInputNotEmpty(
                binding.layoutSecretAnswer,
                getString(R.string.error_text_empty, binding.layoutSecretAnswer.hint)
            )
        }

        // Button Listener
        binding.buttonCreateLogin.setOnClickListener {
            if (saveData(view)) {
                // Navigate if data saved to Shared Preferences successfully.
                view.findNavController().navigate(R.id.action_createLoginFragment_to_loginFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Sets all Text Input to error enabled along with their error message.
     */
    private fun enableAllErrorText() {
        binding.layoutPasscode.isErrorEnabled = true
        binding.layoutRepeatPasscode.isErrorEnabled = true
        binding.layoutSecretQuestion.isErrorEnabled = true
        binding.layoutSecretAnswer.isErrorEnabled = true
        binding.layoutPasscode.error = getString(R.string.error_passcode_length)
        binding.layoutRepeatPasscode.error = getString(R.string.error_passcode_length)
        binding.layoutSecretQuestion.error =
            getString(R.string.error_text_empty, binding.layoutSecretQuestion.hint)
        binding.layoutSecretAnswer.error =
            getString(R.string.error_text_empty, binding.layoutSecretAnswer.hint)
    }

    /**
     * Persist the passcode, secret question, and secret answer to SharedPreferences if there are
     * no errors present in the form. Once it is successfully saved in SharedPreferences,
     * navigates to the Login Fragment. Otherwise, display appropriate error message as a Toast.
     *
     * Returns whether saving to SharedPreferences was successful or not.
     */
    private fun saveData(view: View): Boolean {
        val passcode = binding.inputPasscode.text.toString()
        val secretQuestion = binding.inputSecretQuestion.text.toString()
        val secretAnswer = binding.inputSecretAnswer.text.toString()

        if (isNoneTextInputLayoutErrorEnabled(
                binding.layoutPasscode,
                binding.layoutRepeatPasscode,
                binding.layoutSecretQuestion,
                binding.layoutPasscode
            )
        ) {
            with(sharedPreferences.edit()) {
                putString(PASSCODE_KEY, passcode)
                putString(SECRET_QUESTION_KEY, secretQuestion)
                putString(SECRET_ANSWER_KEY, secretAnswer)
                return commit()
            }
        }

        Toast.makeText(
            requireContext(),
            getString(R.string.error_save_unsuccessful),
            Toast.LENGTH_SHORT
        ).show()
        return false
    }
}