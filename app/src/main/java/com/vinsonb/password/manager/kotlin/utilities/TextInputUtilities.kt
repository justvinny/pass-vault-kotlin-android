package com.vinsonb.password.manager.kotlin.utilities

import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class TextInputUtilities {

    companion object {
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
        fun checkInputTextMatches(
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
            return textInputLayout.none {  it.isErrorEnabled }
        }
    }
}