package com.vinsonb.password.manager.kotlin.fragments.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vinsonb.password.manager.kotlin.R
import com.vinsonb.password.manager.kotlin.adapter.CreditAdapter

class CreditsDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = MaterialAlertDialogBuilder(it)
            val inflater = requireActivity().layoutInflater;
            val view = inflater.inflate(R.layout.dialog_credits, null, false)

            val recyclerViewCredits =
                view.findViewById<RecyclerView>(R.id.recycler_view_credits)
            recyclerViewCredits.adapter = CreditAdapter(requireActivity())

            builder
                .setView(view)
                .setNegativeButton(R.string.button_close) { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    companion object {
        val TAG = CreditsDialog::class.simpleName
    }
}