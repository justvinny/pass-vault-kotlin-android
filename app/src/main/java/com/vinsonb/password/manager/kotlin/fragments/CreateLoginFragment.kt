package com.vinsonb.password.manager.kotlin.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import com.google.android.material.textfield.TextInputLayout
import com.vinsonb.password.manager.kotlin.Constants.Companion.Password.PASSCODE_MAX_LENGTH
import com.vinsonb.password.manager.kotlin.Constants.Companion.Password.SharedPreferenceKeys.PASSCODE_KEY
import com.vinsonb.password.manager.kotlin.Constants.Companion.Password.SharedPreferenceKeys.SECRET_ANSWER_KEY
import com.vinsonb.password.manager.kotlin.Constants.Companion.Password.SharedPreferenceKeys.SECRET_QUESTION_KEY
import com.vinsonb.password.manager.kotlin.R
import com.vinsonb.password.manager.kotlin.databinding.FragmentCreateLoginBinding

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

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(view.context)

        enableAllErrorText()

        // Text Input Listeners
        binding.inputPasscode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                checkPasscodeLength(binding.layoutPasscode)
            }
        })

        binding.inputRepeatPasscode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (checkPasscodeLength(binding.layoutRepeatPasscode)) {
                    checkPasscodeMatches()
                }
            }
        })

        binding.inputSecretQuestion.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                checkInputNotEmpty(binding.layoutSecretQuestion)
            }
        })

        binding.inputSecretAnswer.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                checkInputNotEmpty(binding.layoutSecretAnswer)
            }
        })

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
        binding.layoutSecretQuestion.error = getString(R.string.error_text_empty)
        binding.layoutSecretAnswer.error = getString(R.string.error_text_empty)
    }

    /**
     * Checks passcode is of required length.
     * Otherwise, display appropriate error message on the Text Input.
     */
    private fun checkPasscodeLength(passcode: TextInputLayout): Boolean {
        val passcodeText = passcode.editText?.text.toString()

        if (passcodeText.length < PASSCODE_MAX_LENGTH) {
            passcode.isErrorEnabled = true
            passcode.error = getString(R.string.error_passcode_length)
            return false
        }
        passcode.isErrorEnabled = false
        return true
    }

    /**
     * Checks passcode matches with passcode2.
     * Otherwise, display appropriate error message on the Text Input.
     */
    private fun checkPasscodeMatches() {
        val passcode = binding.inputPasscode.text.toString()
        val passcode2 = binding.inputRepeatPasscode.text.toString()

        if (passcode != passcode2) {
            binding.layoutRepeatPasscode.isErrorEnabled = true
            binding.layoutRepeatPasscode.error = getString(R.string.error_passcode_must_match)
        } else {
            binding.layoutRepeatPasscode.isErrorEnabled = false
        }
    }

    /**
     * Checks if Text Input is not empty.
     * Otherwise, display appropriate error message on the Text Input.
     */
    private fun checkInputNotEmpty(textInputLayout: TextInputLayout) {
        val text = textInputLayout.editText?.text.toString()

        if (text.isEmpty()) {
            textInputLayout.isErrorEnabled = true
            textInputLayout.error = getString(R.string.error_text_empty)
        } else {
            textInputLayout.isErrorEnabled = false
        }
    }

    /**
     * Save the passcode, secret question, and secret answer to SharedPreferences if they pass
     * all the checks. Once it is successfully saved in SharedPreferences, navigates to the Login Fragment.
     * Otherwise, display appropriate error message as a Toast.
     */
    private fun saveData(view: View): Boolean {
        val passcode = binding.inputPasscode.text.toString()
        val secretQuestion = binding.inputSecretQuestion.text.toString()
        val secretAnswer = binding.inputSecretAnswer.text.toString()

        if (passcode.isNotBlank() && secretQuestion.isNotBlank() && secretAnswer.isNotBlank()) {
            val sharedPreferences = activity?.getPreferences(Context.MODE_PRIVATE) ?: return false
            with(sharedPreferences.edit()) {
                putString(PASSCODE_KEY, passcode)
                putString(SECRET_QUESTION_KEY, secretQuestion)
                putString(SECRET_ANSWER_KEY, secretAnswer)
                return commit()
            }
        }

        Toast.makeText(
            view.context,
            getString(R.string.error_passcode_not_saved),
            Toast.LENGTH_SHORT
        ).show()
        return false
    }
}