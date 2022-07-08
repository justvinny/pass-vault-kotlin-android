package com.vinsonb.password.manager.kotlin.fragments.dialogs

import android.app.Dialog
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.vinsonb.password.manager.kotlin.R
import com.vinsonb.password.manager.kotlin.utilities.Constants.Password.SharedPreferenceKeys.SECRET_ANSWER_KEY
import com.vinsonb.password.manager.kotlin.utilities.Constants.Password.SharedPreferenceKeys.SECRET_QUESTION_KEY

class VerifySecretDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = MaterialAlertDialogBuilder(it)
            val inflater = requireActivity().layoutInflater;
            val view = inflater.inflate(R.layout.dialog_verify_secret, null, false)
            val preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

            // TextInputLayout
            val layoutVerifySecretAnswer =
                view.findViewById<TextInputLayout>(R.id.layout_dialog_verify_secret_answer)
            layoutVerifySecretAnswer.hint = preferences.getString(SECRET_QUESTION_KEY, "")

            // TextInputEditText
            val inputVerifySecretAnswer =
                view.findViewById<TextInputEditText>(R.id.input_dialog_verify_secret_answer)

            // Create Dialog
            builder
                .setView(view)
                .setNegativeButton(R.string.button_close) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(R.string.button_verify) { _, _ ->
                    if (verifySecretAnswer(preferences, inputVerifySecretAnswer)) {
                        ChangePasscodeDialog().show(parentFragmentManager, ChangePasscodeDialog.TAG)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            requireContext().getText(R.string.error_wrong_secret_answer),
                            Toast.LENGTH_SHORT
                        ).show()
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

    companion object {
        val TAG = VerifySecretDialog::class.simpleName
    }
}