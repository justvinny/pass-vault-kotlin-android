package com.vinsonb.password.manager.kotlin.adapter

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vinsonb.password.manager.kotlin.R
import com.vinsonb.password.manager.kotlin.database.enitities.Account
import com.vinsonb.password.manager.kotlin.utilities.ClipboardUtilities.copyToClipboard

private const val CLIP_LABEL = "Account Password"

class AccountAdapter(
    private val context: Context,
    private val accountsDialog: Dialog = Dialog(context),
    private var accountList: MutableList<Account> = mutableListOf()
) : RecyclerView.Adapter<AccountAdapter.ViewHolder>() {

    init {
        accountsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        accountsDialog.setContentView(R.layout.dialog_account)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.account_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textViewPlatform.text = accountList[position].platform
        holder.textViewUsername.text = accountList[position].username

        holder.iconMaximizeAccount.setOnClickListener {
            val textViewPlatform = accountsDialog.findViewById<TextView>(R.id.text_dialog_platform)
            textViewPlatform.text = accountList[position].platform

            val textViewUsername = accountsDialog.findViewById<TextView>(R.id.text_dialog_username)
            textViewUsername.text = accountList[position].username

            val inputPassword = accountsDialog.findViewById<EditText>(R.id.input_dialog_password)
            inputPassword.setText(accountList[position].password)
            inputPassword.setSelection(accountList[position].password.length)

            val buttonClose = accountsDialog.findViewById<Button>(R.id.button_accounts_dialog_close)
            buttonClose.setOnClickListener {
                accountsDialog.dismiss()
            }

            accountsDialog.show()
        }

        holder.iconCopy.setOnClickListener {
            copyToClipboard(
                holder.iconCopy.context,
                CLIP_LABEL,
                accountList[position].password,
                message = " password for ${accountList[position].username} "
            )
        }
    }

    override fun getItemCount(): Int {
        return accountList.size
    }

    fun updateAccountList(accountList: List<Account>) {
        this.accountList.clear()
        this.accountList.addAll(accountList)
        notifyDataSetChanged() // TODO Optimise for performance
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewPlatform: TextView
        val textViewUsername: TextView
        val iconCopy: ImageView
        val iconMaximizeAccount: ImageView

        init {
            textViewPlatform = view.findViewById(R.id.text_view_platform)
            textViewUsername = view.findViewById(R.id.text_view_username)
            iconCopy = view.findViewById(R.id.icon_copy_password)
            iconMaximizeAccount = view.findViewById(R.id.icon_maximize_account)
        }
    }
}