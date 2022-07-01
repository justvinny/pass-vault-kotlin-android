package com.vinsonb.password.manager.kotlin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.vinsonb.password.manager.kotlin.R
import com.vinsonb.password.manager.kotlin.database.enitities.Account
import com.vinsonb.password.manager.kotlin.fragments.dialogs.AccountDialog
import com.vinsonb.password.manager.kotlin.utilities.ClipboardUtilities.CLIP_PASSWORD_LABEL
import com.vinsonb.password.manager.kotlin.utilities.ClipboardUtilities.copyToClipboard
import com.vinsonb.password.manager.kotlin.viewmodels.AccountViewModel

class AccountAdapter (
    private val fragmentManager: FragmentManager,
    private val viewModel: AccountViewModel,
    private var accountList: MutableList<Account> = mutableListOf()
) : RecyclerView.Adapter<AccountAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.account_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textViewPlatform.text = accountList[position].platform
        holder.textViewUsername.text = accountList[position].username

        holder.iconMaximizeAccount.setOnClickListener {
            val dialog = AccountDialog(
                accountList[position],
                viewModel
            )
            dialog.show(fragmentManager, AccountDialog.TAG)
        }

        holder.iconCopy.setOnClickListener {
            copyToClipboard(
                holder.iconCopy.context,
                CLIP_PASSWORD_LABEL,
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
        notifyDataSetChanged()
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