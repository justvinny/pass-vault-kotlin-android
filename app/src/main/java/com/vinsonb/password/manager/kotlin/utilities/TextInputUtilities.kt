package com.vinsonb.password.manager.kotlin.utilities

import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

object TextInputUtilities {
    /**
     * Checks if Text Input is not empty.
     * Otherwise, display appropriate error message on the Text Input.
     */
    fun checkInputNotEmpty(textInputLayout: TextInputLayout, errorMessage: String) {
        val text = textInputLayout.editText?.text.toString()

        if (text.isEmpty()) {
            textInputLayout.isErrorEnabled = true
            textInputLayout.error = errorMessage
        } else {
            textInputLayout.isErrorEnabled = false
        }
    }

    /**
     * Checks two EditText fields have the same input.
     * Otherwise, display appropriate error message on the Text Input.
     */
    private fun checkInputTextMatches(
        editText: TextInputEditText,
        editText2: TextInputEditText,
        textInputLayout: TextInputLayout,
        errorMessage: String
    ) {
        val passcode = editText.text.toString()
        val passcode2 = editText2.text.toString()

        if (passcode != passcode2) {
            textInputLayout.isErrorEnabled = true
            textInputLayout.error = errorMessage
        } else {
            textInputLayout.isErrorEnabled = false
        }
    }

    /**
     * Checks if all fields do not have an error message and that all input is valid.
     *
     * Returns whether all the TextInputLayouts passed as arguments have error enabled or not.
     */
    fun isNoneTextInputLayoutErrorEnabled(vararg textInputLayout: TextInputLayout): Boolean {
        return textInputLayout.none { it.isErrorEnabled }
    }

    /**
     * Checks passcode is of required length.
     * Otherwise, display appropriate error message on the Text Input.
     *
     * Returns whether the passcode length is valid or not.
     */
    fun checkPasscodeLength(passcode: TextInputLayout, errorMessage: String): Boolean {
        val passcodeText = passcode.editText?.text.toString()

        if (passcodeText.length < Constants.Password.PASSCODE_MAX_LENGTH) {
            passcode.isErrorEnabled = true
            passcode.error = errorMessage
            return false
        }
        passcode.isErrorEnabled = false
        return true
    }

    /**
     * Validates repeat passcode EditText for length and passcode match.
     */
    fun validateRepeatPasscode(
        layoutRepeatPasscode: TextInputLayout,
        inputPasscode: TextInputEditText,
        inputRepeatPasscode: TextInputEditText,
        errorMessageLength: String,
        errorMessageMustMatch: String
    ) {
        if (checkPasscodeLength(layoutRepeatPasscode, errorMessageLength)) {
            checkInputTextMatches(
                inputPasscode,
                inputRepeatPasscode,
                layoutRepeatPasscode,
                errorMessageMustMatch
            )
        }
    }
}