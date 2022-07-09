package com.vinsonb.password.manager.kotlin.fragments.dialogs

import android.app.Dialog
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.vinsonb.password.manager.kotlin.R
import com.vinsonb.password.manager.kotlin.utilities.Constants.Password.SharedPreferenceKeys.PASSCODE_KEY
import com.vinsonb.password.manager.kotlin.utilities.Constants.Password.SharedPreferenceKeys.SECRET_ANSWER_KEY
import com.vinsonb.password.manager.kotlin.utilities.Constants.Password.SharedPreferenceKeys.SECRET_QUESTION_KEY
import com.vinsonb.password.manager.kotlin.utilities.TextInputUtilities.checkPasscodeLength
import com.vinsonb.password.manager.kotlin.utilities.TextInputUtilities.isNoneTextInputLayoutErrorEnabled
import com.vinsonb.password.manager.kotlin.utilities.TextInputUtilities.validateRepeatPasscode

class ForgotPasswordDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = MaterialAlertDialogBuilder(it)
            val inflater = requireActivity().layoutInflater;
            val view = inflater.inflate(R.layout.dialog_forgot_password, null, false)
            val preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

            // TextInputLayout
            val layoutVerifySecretAnswer =
                view.findViewById<TextInputLayout>(R.id.layout_dialog_secret_answer)
            layoutVerifySecretAnswer.hint = preferences.getString(SECRET_QUESTION_KEY, "")

            val layoutPasscode =
                view.findViewById<TextInputLayout>(R.id.layout_dialog_change_passcode)

            val layoutRepeatPasscode =
                view.findViewById<TextInputLayout>(R.id.layout_dialog_change_passcode_repeat)
            enableAllErrorText(layoutPasscode, layoutRepeatPasscode)

            // TextInputEditText
            val inputVerifySecretAnswer =
                view.findViewById<TextInputEditText>(R.id.input_dialog_secret_answer)

            val inputPasscode =
                view.findViewById<TextInputEditText>(R.id.input_dialog_change_passcode)

            val inputRepeatPasscode =
                view.findViewById<TextInputEditText>(R.id.input_dialog_change_passcode_repeat)

            // Listeners
            inputPasscode.addTextChangedListener {
                checkPasscodeLength(
                    layoutPasscode,
                    requireContext().getString(R.string.error_passcode_length)
                )
                validateRepeatPasscode(
                    layoutRepeatPasscode,
                    inputPasscode,
                    inputRepeatPasscode,
                    requireContext().getString(R.string.error_passcode_length),
                    requireContext().getString(R.string.error_passcode_must_match)
                )
            }

            inputRepeatPasscode.addTextChangedListener {
                validateRepeatPasscode(
                    layoutRepeatPasscode,
                    inputPasscode,
                    inputRepeatPasscode,
                    requireContext().getString(R.string.error_passcode_length),
                    requireContext().getString(R.string.error_passcode_must_match)
                )
            }

            // Create Dialog
            builder
                .setView(view)
                .setNegativeButton(R.string.button_close) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(R.string.button_reset) { _, _ ->
                    if (!verifySecretAnswer(preferences, inputVerifySecretAnswer)) {
                        Toast.makeText(
                            requireContext(),
                            requireContext().getText(R.string.error_wrong_secret_answer),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (!isNoneTextInputLayoutErrorEnabled(
                            layoutPasscode,
                            layoutRepeatPasscode
                        )
                    ) {
                        Toast.makeText(
                            requireContext(),
                            requireContext().getText(R.string.error_save_unsuccessful),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        changePasscode(preferences, inputPasscode)
                    }
                }
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    /**
     * Checks if the entered secret answer matches the one saved in SharedPreferences.
     *
     * returns whether or not secret answer entered matches the one saved in SharedPreferences.
     */
    private fun verifySecretAnswer(
        preferenceManager: SharedPreferences,
        inputVerifySecretAnswer: TextInputEditText
    ): Boolean {
        val actualSecretAnswer = preferenceManager.getString(SECRET_ANSWER_KEY, "")
        val enteredSecretAnswer = inputVerifySecretAnswer.text.toString()

        return !(actualSecretAnswer.isNullOrBlank() ||
                enteredSecretAnswer.isBlank()) &&
                actualSecretAnswer == enteredSecretAnswer
    }

    /**
     * Enables all error text for all our TextInputLayout views.
     */
    private fun enableAllErrorText(
        layoutPasscode: TextInputLayout,
        layoutRepeatPasscode: TextInputLayout
    ) {
        layoutPasscode.isErrorEnabled = true
        layoutRepeatPasscode.isErrorEnabled = true
        layoutPasscode.error = requireContext().getString(R.string.error_passcode_length)
        layoutRepeatPasscode.error = requireContext().getString(R.string.error_passcode_length)
    }

    /**
     * Change the passcode saved in SharedPreferences
     */
    private fun changePasscode(preferences: SharedPreferences, inputPasscode: TextInputEditText) {
        with(preferences.edit()) {
            putString(PASSCODE_KEY, inputPasscode.text.toString())
            if (commit()) {
                Toast.makeText(
                    requireContext(),
                    requireContext().getText(R.string.success_passcode_changed),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    companion object {
        val TAG = ForgotPasswordDialog::class.simpleName
    }
}