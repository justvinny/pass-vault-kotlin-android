package com.vinsonb.password.manager.kotlin.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.vinsonb.password.manager.kotlin.R
import com.vinsonb.password.manager.kotlin.databinding.FragmentLoginBinding
import com.vinsonb.password.manager.kotlin.utilities.Constants.Companion.Password.PASSCODE_MAX_LENGTH
import com.vinsonb.password.manager.kotlin.utilities.Constants.Companion.Password.SharedPreferenceKeys.AUTHENTICATED_KEY
import com.vinsonb.password.manager.kotlin.utilities.Constants.Companion.Password.SharedPreferenceKeys.PASSCODE_KEY
import com.vinsonb.password.manager.kotlin.utilities.Constants.Companion.Password.SharedPreferenceKeys.SECRET_ANSWER_KEY
import com.vinsonb.password.manager.kotlin.utilities.Constants.Companion.Password.SharedPreferenceKeys.SECRET_QUESTION_KEY
import com.vinsonb.password.manager.kotlin.viewmodels.LoginViewModel

private const val TAG = "LoginFragment"

class LoginFragment : Fragment(R.layout.fragment_login) {
    private val viewModel: LoginViewModel by lazy {
        LoginViewModel()
    }

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private var buttonList: MutableList<Button> = mutableListOf()
    private var circleList: MutableList<ImageView> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Number 0 - 9 buttons
        populateButtonList()
        buttonList.forEach { it ->
            it.setOnClickListener {
                numberButtonClick(it)
            }
        }

        // Clear button
        binding.buttonClear.setOnClickListener {
            viewModel.clearLastDigit()
        }

        // TODO - Forgot password button

        // ImageViews 5 circles
        val sharedPreferences = activity?.getPreferences(Context.MODE_PRIVATE)
        populateCircleList()
        viewModel.getPassword().observe(viewLifecycleOwner) {
            setCircleIcon(it)

            // Try to login when passcode reaches 5 digits.
            if (it.size == PASSCODE_MAX_LENGTH) {
                if (login(it, sharedPreferences)) {
                    viewModel.clearAllDigits()
                    view.findNavController()
                        .navigate(R.id.action_loginFragment_to_viewAccountsFragment)
                } else {
                    Toast.makeText(view.context, R.string.error_wrong_passcode, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        if (isAccountNotCreated(sharedPreferences)) {
            view.findNavController().navigate(R.id.action_loginFragment_to_createLoginFragment2)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Check if account is not already created by checking SharedPreferences if
     * values: passcode, secret question, and secret answer exist.
     */
    private fun isAccountNotCreated(sharedPreferences: SharedPreferences?): Boolean {
        val passcode = sharedPreferences?.getString(PASSCODE_KEY, "")
        val secretQuestion =
            sharedPreferences?.getString(SECRET_QUESTION_KEY, "")
        val secretAnswer = sharedPreferences?.getString(SECRET_ANSWER_KEY, "")
        return passcode.isNullOrBlank() && secretQuestion.isNullOrBlank() && secretAnswer.isNullOrBlank()
    }

    /**
     * Puts all number buttons in one collection for ease of access.
     */
    private fun populateButtonList() {
        buttonList.add(binding.button0)
        buttonList.add(binding.button1)
        buttonList.add(binding.button2)
        buttonList.add(binding.button3)
        buttonList.add(binding.button4)
        buttonList.add(binding.button5)
        buttonList.add(binding.button6)
        buttonList.add(binding.button7)
        buttonList.add(binding.button8)
        buttonList.add(binding.button9)
    }

    /**
     * Click listener for number buttons to enter the digit clicked as part of password.
     */
    private fun numberButtonClick(view: View) {
        val digit = (view as Button).text.toString().toInt()
        viewModel.addDigitToPassword(digit)
    }

    /**
     * Puts all circles in one collection for ease of access.
     */
    private fun populateCircleList() {
        circleList.add(binding.circle1)
        circleList.add(binding.circle2)
        circleList.add(binding.circle3)
        circleList.add(binding.circle4)
        circleList.add(binding.circle5)
    }

    /**
     * Dynamically fill / un-fill circle icons depending on number press and clear.
     */
    private fun setCircleIcon(passcode: ArrayDeque<Int>) {
        for (i in 0 until PASSCODE_MAX_LENGTH) {
            if (i < passcode.size) {
                circleList[i].setImageResource(R.drawable.ic_circle_filled)
            } else {
                circleList[i].setImageResource(R.drawable.ic_circle_outlined)
            }
        }
    }

    /**
     * Logs in the user by changing the authentication value in SharedPreferences to true if
     * the passcode entered matches the value stored.
     *
     * returns whether the user successfully logged in or not.
     */
    private fun login(passcode: ArrayDeque<Int>, sharedPreferences: SharedPreferences?): Boolean {
        val savedPasscode = sharedPreferences?.getString(PASSCODE_KEY, "")
        val enteredPasscode = passcode.joinToString(separator = "")
        if (savedPasscode == enteredPasscode) {
            with(sharedPreferences.edit()) {
                putBoolean(AUTHENTICATED_KEY, true)
                return (commit())
            }
        }
        return false
    }
}