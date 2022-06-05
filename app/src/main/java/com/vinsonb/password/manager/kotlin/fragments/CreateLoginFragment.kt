package com.vinsonb.password.manager.kotlin.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log.d
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
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
        binding.passcode.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                checkPasscodeLength(binding.passcode)
            }
        })

        binding.passcode2.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (checkPasscodeLength(binding.passcode2)) {
                    checkPasscodeMatches()
                }
            }
        })

        binding.secretQuestion.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                checkInputNotEmpty(binding.secretQuestion)
            }
        })

        binding.secretAnswer.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                checkInputNotEmpty(binding.secretAnswer)
            }
        })

        // Button Listener
        binding.buttonCreateLogin.setOnClickListener {
            saveData(view.context)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun enableAllErrorText() {
        binding.passcode.isErrorEnabled = true
        binding.passcode2.isErrorEnabled = true
        binding.secretQuestion.isErrorEnabled = true
        binding.secretAnswer.isErrorEnabled = true
        binding.passcode.error = getString(R.string.error_passcode_length)
        binding.passcode2.error = getString(R.string.error_passcode_length)
        binding.secretQuestion.error = getString(R.string.error_text_empty)
        binding.secretAnswer.error = getString(R.string.error_text_empty)
    }

    private fun checkPasscodeLength(passcode: TextInputLayout): Boolean {
        val passcodeText = passcode.editText?.text.toString()
        if (passcodeText.length < 5) {
            passcode.isErrorEnabled = true
            passcode.error = getString(R.string.error_passcode_length)
            return false
        }
        passcode.isErrorEnabled = false
        return true
    }

    private fun checkPasscodeMatches() {
        val passcode = binding.passcode.editText?.text.toString()
        val passcode2 = binding.passcode2.editText?.text.toString()
        if (passcode != passcode2) {
            binding.passcode2.isErrorEnabled = true
            binding.passcode2.error = getString(R.string.error_passcode_must_match)
        } else {
            binding.passcode2.isErrorEnabled = false
        }
    }

    private fun checkInputNotEmpty(textInputLayout: TextInputLayout) {
        val text = textInputLayout.editText?.text.toString()
        if (text.isEmpty()) {
            textInputLayout.isErrorEnabled = true
            textInputLayout.error = getString(R.string.error_text_empty)
        } else {
            textInputLayout.isErrorEnabled = false
        }
    }

    private fun saveData(context: Context) {
        val passcode = binding.passcode.editText?.text.toString()
        val secretQuestion = binding.secretQuestion.editText?.text.toString()
        val secretAnswer = binding.secretAnswer.editText?.text.toString()

        if (passcode.isNotBlank() && secretQuestion.isNotBlank() && secretAnswer.isNotBlank()) {
            val sharedPreferences = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
            with(sharedPreferences.edit()) {
                putString(PASSCODE_KEY, passcode)
                putString(SECRET_QUESTION_KEY, secretQuestion)
                putString(SECRET_ANSWER_KEY, secretAnswer)
                apply()
            }
        } else {
            Toast.makeText(context, getString(R.string.error_passcode_not_saved), Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val PASSCODE_KEY = "passcode"
        const val SECRET_QUESTION_KEY = "secret question"
        const val SECRET_ANSWER_KEY = "secret answer"
    }
}