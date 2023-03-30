package com.vinsonb.password.manager.kotlin.fragments.dialogs

import android.app.Dialog
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vinsonb.password.manager.kotlin.R
import com.vinsonb.password.manager.kotlin.database.enitities.Account
import com.vinsonb.password.manager.kotlin.utilities.ClipboardUtilities
import com.vinsonb.password.manager.kotlin.utilities.ClipboardUtilities.CLIP_PASSWORD_LABEL
import com.vinsonb.password.manager.kotlin.utilities.ClipboardUtilities.CLIP_USERNAME_LABEL
import com.vinsonb.password.manager.kotlin.viewmodels.AccountViewModel

class AccountDialog(
    private val account: Account,
    private val viewModel: AccountViewModel
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = MaterialAlertDialogBuilder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.dialog_account, null, false)

            val textViewPlatform = view.findViewById<TextView>(R.id.text_accounts_dialog_platform)
            textViewPlatform.text = account.platform

            val textViewUsername = view.findViewById<TextView>(R.id.text_accounts_dialog_username)
            textViewUsername.text = account.username

            val inputPassword = view.findViewById<EditText>(R.id.input_accounts_dialog_password)
            inputPassword.setText(account.password)
            inputPassword.setSelection(account.password.length)

            // Click Listeners
            val iconEdit = view.findViewById<ImageView>(R.id.icon_accounts_dialog_edit)
            iconEdit.setOnClickListener {
                toggleEditAndSave(inputPassword, iconEdit)
            }

            val iconDelete = view.findViewById<ImageView>(R.id.icon_accounts_dialog_delete)
            iconDelete.setOnClickListener {
                viewModel.deleteAccount(account)
                dismiss()
            }

            val iconCopyUsername =
                view.findViewById<ImageView>(R.id.icon_accounts_dialog_copy_username)
            iconCopyUsername.setOnClickListener {
                ClipboardUtilities.copyToClipboard(
                    context = requireContext(),
                    clipLabel = CLIP_USERNAME_LABEL,
                    toCopy = account.username,
                    username = account.username,
                )
            }

            val iconSeePassword =
                view.findViewById<ImageView>(R.id.icon_accounts_dialog_see_password)
            iconSeePassword.setOnClickListener {
                togglePasswordVisibility(inputPassword)
            }

            val iconCopyPassword =
                view.findViewById<ImageView>(R.id.icon_accounts_dialog_copy_password)
            iconCopyPassword.setOnClickListener {
                ClipboardUtilities.copyToClipboard(
                    context = requireContext(),
                    clipLabel = CLIP_PASSWORD_LABEL,
                    toCopy = account.password,
                    username = account.username,
                )
            }

            // Create Dialog
            builder
                .setView(view)
                .setNegativeButton(R.string.button_close) { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    /**
     * Allow / disable editing for password and save the changes if any.
     */
    private fun toggleEditAndSave(inputPassword: EditText, iconEdit: ImageView) {
        inputPassword.isEnabled = !inputPassword.isEnabled
        if (inputPassword.isEnabled) {
            iconEdit.setImageResource(R.drawable.ic_save)
        } else {
            iconEdit.setImageResource(R.drawable.ic_edit)
            viewModel.updateAccount(Account(account.platform, account.username, inputPassword.text.toString()))
        }
    }

    /**
     * Shows or hides the password.
     */
    private fun togglePasswordVisibility(inputPassword: EditText) {
        if (inputPassword.transformationMethod is PasswordTransformationMethod) {
            inputPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
        } else {
            inputPassword.transformationMethod = PasswordTransformationMethod.getInstance()
        }
    }

    companion object {
        val TAG = AccountDialog::class.simpleName
    }
}