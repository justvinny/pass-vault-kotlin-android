package com.vinsonb.password.manager.kotlin.fragments.dialogs

import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vinsonb.password.manager.kotlin.R

class AccountDialog(
    private val platform: String,
    private val username: String,
    private val password: String
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = MaterialAlertDialogBuilder(it)
            val inflater = requireActivity().layoutInflater;
            val view = inflater.inflate(R.layout.dialog_account, null, false)

            val textViewPlatform = view.findViewById<TextView>(R.id.text_accounts_dialog_platform)
            textViewPlatform.text = platform

            val textViewUsername = view.findViewById<TextView>(R.id.text_accounts_dialog_username)
            textViewUsername.text = username

            val inputPassword = view.findViewById<EditText>(R.id.input_accounts_dialog_password)
            inputPassword.setText(password)
            inputPassword.setSelection(password.length)

            builder
                .setView(view)
                .setNegativeButton(R.string.button_close) { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    companion object {
        val TAG = AccountDialog::class.simpleName
    }
}