package com.vinsonb.password.manager.kotlin.fragments.dialogs

import android.app.Dialog
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
import com.vinsonb.password.manager.kotlin.utilities.TextInputUtilities
import com.vinsonb.password.manager.kotlin.utilities.TextInputUtilities.checkPasscodeLength
import com.vinsonb.password.manager.kotlin.utilities.TextInputUtilities.isNoneTextInputLayoutErrorEnabled

class ChangePasscodeDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = MaterialAlertDialogBuilder(it)
            val inflater = requireActivity().layoutInflater;
            val view = inflater.inflate(R.layout.dialog_change_passcode, null, false)
            val preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

            // TextInputLayouts
            val layoutPasscode =
                view.findViewById<TextInputLayout>(R.id.layout_dialog_change_passcode)

            val layoutRepeatPasscode =
                view.findViewById<TextInputLayout>(R.id.layout_dialog_change_passcode_repeat)
            enableAllErrorText(layoutPasscode, layoutRepeatPasscode)

            // EditText listeners
            val inputPasscode =
                view.findViewById<TextInputEditText>(R.id.input_dialog_change_passcode)
            inputPasscode.addTextChangedListener {
                checkPasscodeLength(
                    layoutPasscode,
                    requireContext().getString(R.string.error_passcode_length)
                )
            }

            val inputRepeatPasscode =
                view.findViewById<TextInputEditText>(R.id.input_dialog_change_passcode_repeat)
            inputRepeatPasscode.addTextChangedListener {
                if (checkPasscodeLength(
                        layoutRepeatPasscode,
                        requireContext().getString(R.string.error_passcode_length)
                    )
                ) {
                    TextInputUtilities.checkInputTextMatches(
                        inputPasscode,
                        inputRepeatPasscode,
                        layoutRepeatPasscode,
                        requireContext().getString(R.string.error_passcode_must_match)
                    )
                }
            }

            // Create Dialog
            builder
                .setView(view)
                .setNegativeButton(R.string.button_close) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(R.string.button_reset) { _, _ ->
                    if (isNoneTextInputLayoutErrorEnabled(layoutPasscode, layoutRepeatPasscode)) {
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
                    } else {
                        Toast.makeText(
                            requireContext(),
                            requireContext().getText(R.string.error_save_unsuccessful),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
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

    companion object {
        val TAG = ChangePasscodeDialog::class.simpleName
    }
}